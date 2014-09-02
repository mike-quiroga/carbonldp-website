package com.base22.carbon.ldp.web.handlers;

import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import com.base22.carbon.Carbon;
import com.base22.carbon.CarbonException;
import com.base22.carbon.HttpHeaders;
import com.base22.carbon.authorization.acl.ACLSystemResource;
import com.base22.carbon.authorization.acl.ACLSystemResourceFactory;
import com.base22.carbon.ldp.ModelUtil;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class PUTRequestHandler extends AbstractCreationRequestHandler {
	private String ifMatchHeader;

	private ACLSystemResource requestAclSR;

	private String targetETag;

	public ResponseEntity<Object> handlePut(@PathVariable("application") String applicationIdentifier, HttpServletRequest request,
			HttpServletResponse response, HttpEntity<byte[]> entity) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handlePut()");
		}

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("-- handlePut() > Request info: {}", HTTPUtil.printRequestInfo(request));
		}

		try {
			return doHandlePUT(applicationIdentifier, request, response, entity);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}

	private ResponseEntity<Object> doHandlePUT(String applicationIdentifier, HttpServletRequest request, HttpServletResponse response, HttpEntity<byte[]> entity)
			throws CarbonException {
		resetGlobalVariables();

		this.request = request;
		this.response = response;

		populateDataset();
		populateLanguage();
		populateTargetURI();
		populateRequestURI();

		populateEntityBody(entity);
		addDefaultPrefixes();
		saveEntityBodyInString();

		populateTargetURIObject();
		if ( targetDocumentExists() ) {
			return handlePUTExisting();
		} else {
			return handlePUTNonExisting();
		}
	}

	private ResponseEntity<Object> handlePUTNonExisting() throws CarbonException {
		// TODO: FT
		return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
	}

	private ResponseEntity<Object> handlePUTExisting() throws CarbonException {
		populateIfMatchHeader();

		if ( this.ifMatchHeader == null ) {
			return handleNonConditionalPUT();
		} else {
			return handleConditionalPUT();
		}
	}

	private ResponseEntity<Object> handleNonConditionalPUT() throws CarbonException {
		String debugMessage = "An If-Match header wasn't provided.";

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handleNonConditionalPUT() > {}", debugMessage);
		}

		ErrorResponseFactory factory = new ErrorResponseFactory();
		ErrorResponse errorObject = factory.create();
		errorObject.setFriendlyMessage(debugMessage);
		errorObject.setDebugMessage(debugMessage);
		errorObject.addHeaderIssue("If-Match", null, "required", null);

		return HTTPUtil.createErrorResponseEntity(errorObject, HttpStatus.PRECONDITION_REQUIRED);
	}

	private ResponseEntity<Object> handleConditionalPUT() throws CarbonException {
		parseEntityBody(this.targetURI);

		processRequestModel();

		populateRequestRDFSource();
		populateTargetRDFSource();

		// ETag check
		populateTargetETag();
		checkETags();

		populateRequestAclSR();
		if ( requestAclSRIsPresent() ) {
			validateRequestAclSR();
			applyRequestAclSR();
		}

		ModelUtil.removeSystemResources(this.requestModel);

		setNewTargetRDFSourceTimestamps();

		replaceTargetRDFSource();

		response.addHeader(HttpHeaders.LOCATION, this.targetURI);
		response.addHeader(HttpHeaders.ETAG, HTTPUtil.formatWeakETag(this.requestRDFSource.getETag()));
		for (String type : this.requestRDFSource.getLinkTypes()) {
			response.addHeader(HttpHeaders.LINK, type);
		}

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handleConditionalPUT() > The resource: {} was modified.", requestURI);
		}

		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	protected void resetGlobalVariables() {
		super.resetGlobalVariables();

		this.ifMatchHeader = null;

		this.requestAclSR = null;

		this.targetETag = null;
	}

	private void populateRequestURI() throws CarbonException {
		this.requestURI = this.targetURI;
	}

	private void populateIfMatchHeader() throws CarbonException {
		this.ifMatchHeader = this.request.getHeader(HttpHeaders.IF_MATCH);
	}

	private void processRequestModel() throws CarbonException {
		String resourceURI = null;

		ResIterator resourceIterator = this.requestModel.listSubjects();
		while (resourceIterator.hasNext()) {
			Resource resource = resourceIterator.next();
			resourceURI = resource.getURI();

			// Check if it is a valid URL
			if ( ! HTTPUtil.isValidURL(resourceURI) ) {
				String debugMessage = MessageFormat.format("The resource URI: ''{0}'' isn't a URL.", resourceURI);

				if ( LOG.isDebugEnabled() ) {
					LOG.debug("<< processRequestModel() > {}", debugMessage);
				}

				ErrorResponseFactory factory = new ErrorResponseFactory();
				ErrorResponse errorObject = factory.create();
				errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
				errorObject.setFriendlyMessage(debugMessage);
				errorObject.setDebugMessage(debugMessage);
				errorObject.setEntityBodyIssue(null, debugMessage);

				throw new CarbonException(errorObject);
			} else {
				// Check if the URI isn't outside of the domain/dataset
				if ( ! resourceURI.startsWith(this.targetURI) ) {
					// CASE 4b: The requestURI and the resourceURI share the same server but not the dataset
					// CASE 4c: The requestURI and the resourceURI don't share the same server neither the dataset
					String debugMessage = MessageFormat.format("The resourceURI: ''{0}'' doesn't share its base with the request URI.", resourceURI);

					if ( LOG.isDebugEnabled() ) {
						LOG.debug("<< processRequestModel() > {}", debugMessage);
					}

					ErrorResponseFactory factory = new ErrorResponseFactory();
					ErrorResponse errorObject = factory.create();
					errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
					errorObject.setFriendlyMessage(debugMessage);
					errorObject.setDebugMessage(debugMessage);
					errorObject.setEntityBodyIssue(null, debugMessage);

					throw new CarbonException(errorObject);
				}
			}
			// TODO: Move this
			// Check if it is a document resource (independently resolvable)
			if ( ! (resourceURI.matches("(?:.+)#(?:.+)") || resourceURI.matches("(?:.+)\\" + Carbon.SYSTEM_RESOURCE_SIGN + "(?:.+)")) ) {
				// It is
				// Check if it is the same as the requestURI
				if ( ! resourceURI.equals(this.targetURI) ) {
					// It isn't
					String debugMessage = MessageFormat.format(
							"The entity body contains a document resource with a URI different than the request URI. Resource URI: ''{0}''.", resourceURI);

					if ( LOG.isDebugEnabled() ) {
						LOG.debug("<< processRequestModel() > {}", debugMessage);
					}

					ErrorResponseFactory factory = new ErrorResponseFactory();
					ErrorResponse errorObject = factory.create();
					errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
					errorObject
							.setFriendlyMessage("In a PUT request only one document resource can exist and it needs to be the same as the request URI. Remember POST to parent, PUT to me.");
					errorObject.setDebugMessage(debugMessage);
					errorObject.setEntityBodyIssue(null, debugMessage);

					throw new CarbonException(errorObject);
				}
			} else {
				// It isn't
				// TODO: Check secondary resource URIs
			}
		}
	}

	private void populateTargetETag() throws CarbonException {
		this.targetETag = this.targetRDFSource.getETag();
		if ( this.targetETag == null ) {
			String debugMessage = MessageFormat.format("The resource with URI: ''{0}'', doesn''t have a valid ETag.", requestURI);

			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< populateTargetETag() > {}", debugMessage);
			}
		}
	}

	private void checkETags() throws CarbonException {
		if ( this.targetETag != null ) {
			this.ifMatchHeader = this.ifMatchHeader.contains("\"") ? this.ifMatchHeader.split("\"")[1] : this.ifMatchHeader;
			if ( ! this.targetETag.equals(ifMatchHeader) ) {
				String debugMessage = MessageFormat.format("The If-Match header didn''t match the document resource ETag. If-Match: {0}, ETag: {1}",
						this.ifMatchHeader, this.targetETag);

				if ( LOG.isDebugEnabled() ) {
					LOG.debug("<< handlePut() > {}", debugMessage);
				}

				ErrorResponseFactory factory = new ErrorResponseFactory();
				ErrorResponse errorObject = factory.create();
				errorObject.setHttpStatus(HttpStatus.PRECONDITION_FAILED);
				errorObject.setFriendlyMessage("The resource has been externally modified while processing the request. The request will be aborted.");
				errorObject.setDebugMessage(debugMessage);
				throw new CarbonException(errorObject);
			}
		}
	}

	private void populateRequestAclSR() {
		this.requestAclSR = this.requestRDFSource.getAclSR();
	}

	private boolean requestAclSRIsPresent() {
		return this.requestAclSR != null;
	}

	private void validateRequestAclSR() throws CarbonException {
		ACLSystemResourceFactory factory = new ACLSystemResourceFactory();

		List<String> aclViolations = factory.validate(requestAclSR);
		if ( ! aclViolations.isEmpty() ) {
			StringBuilder violationsBuilder = new StringBuilder();
			violationsBuilder.append("The ACL isn't valid. Violations:");
			for (String violation : aclViolations) {
				violationsBuilder.append("\n\t").append(violation);
			}

			String violations = violationsBuilder.toString();

			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = "The entity body contains an invalid ACL system resource.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< validateRequestAclSR() > {}", debugMessage);
			}

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, violations);

			throw new CarbonException(errorObject);
		}
	}

	private void applyRequestAclSR() throws CarbonException {
		try {
			ldpPermissionService.replaceLDPResourceACL(this.targetURIObject, this.requestAclSR);
		} catch (CarbonException e) {
			if ( e.getErrorObject().getHttpStatus() == null ) {
				e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			throw e;
		}
	}

	private void setNewTargetRDFSourceTimestamps() throws CarbonException {
		DateTime created = this.targetRDFSource.getCreated();
		if ( created != null ) {
			this.requestRDFSource.setTimestamps(created);
		} else {
			this.requestRDFSource.setTimestamps();
		}
	}

	private void replaceTargetRDFSource() throws CarbonException {
		try {
			ldpService.replaceLDPRSource(this.requestRDFSource, this.targetURIObject, this.dataset);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

}
