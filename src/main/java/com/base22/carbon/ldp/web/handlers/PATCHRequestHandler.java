package com.base22.carbon.ldp.web.handlers;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.base22.carbon.CarbonException;
import com.base22.carbon.HttpHeaders;
import com.base22.carbon.apps.Application;
import com.base22.carbon.ldp.models.LDPRSource;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.ldp.patch.PATCHRequest;
import com.base22.carbon.ldp.patch.PATCHRequestFactory;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.shared.Lock;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class PATCHRequestHandler extends AbstractLDPRequestHandler {

	public ResponseEntity<Object> handlePATCHRequest(String appSlug, Model requestModel, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {

		Application application = getApplicationFromContext();

		String targetURI = getTargetURI(request);
		URIObject targetURIObject = getTargetURIObject(targetURI);

		if ( ! targetResourceExists(targetURIObject) ) {
			return handleNonExistentResource(targetURI, requestModel, request, response);
		}

		Resource requestMainResouce = getRequestModelMainResource(requestModel);

		if ( ! patchRequestWasProvided(requestMainResouce) ) {
			return handleNonPATCHRequest(targetURI, requestModel, request, response);
		}

		PATCHRequest patchRequest = getPATCHRequest(requestMainResouce);

		validatePATCHRequest(patchRequest);

		LDPRSource targetRDFSource = getTargetRDFSource(targetURIObject, application);

		String requestETag = getRequestETag(request);

		if ( eTagWasProvided(requestETag) ) {
			return handleConditionalPATCH(targetURIObject, patchRequest, targetRDFSource, requestETag, application, request, response);
		} else {
			return handleNonConditionalPATCH(targetURIObject, patchRequest, targetRDFSource, application, request, response);
		}
	}

	private ResponseEntity<Object> handleNonPATCHRequest(String targetURI, Model requestModel, HttpServletRequest request, HttpServletResponse response) {
		String friendlyMessage = "The request isn't a valid PATCH request.";
		String debugMessage = "The request didn't contain a cp:PATCHRequest.";

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handleNonConditionalPATCH() > {}", debugMessage);
		}

		ErrorResponseFactory factory = new ErrorResponseFactory();
		ErrorResponse errorObject = factory.create();
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject, HttpStatus.PRECONDITION_REQUIRED);

	}

	private ResponseEntity<Object> handleNonConditionalPATCH(URIObject targetURIObject, PATCHRequest patchRequest, LDPRSource targetRDFSource,
			Application application, HttpServletRequest request, HttpServletResponse response) {
		String debugMessage = "An If-Match header wasn't provided.";

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handleNonConditionalPATCH() > {}", debugMessage);
		}

		ErrorResponseFactory factory = new ErrorResponseFactory();
		ErrorResponse errorObject = factory.create();
		errorObject.setFriendlyMessage(debugMessage);
		errorObject.setDebugMessage(debugMessage);
		errorObject.addHeaderIssue("If-Match", null, "required", null);

		return HTTPUtil.createErrorResponseEntity(errorObject, HttpStatus.PRECONDITION_REQUIRED);

	}

	private ResponseEntity<Object> handleConditionalPATCH(URIObject targetURIObject, PATCHRequest patchRequest, LDPRSource targetRDFSource, String requestETag,
			Application application, HttpServletRequest request, HttpServletResponse response) throws CarbonException {

		String targetETag = getTargetETag(targetRDFSource);

		if ( targetETag == null ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx handleConditionalPATCH() > The ");
			}
		} else {
			if ( ! compareETags(requestETag, targetETag) ) {
				return handleNonMatchinETags(targetURIObject, requestETag, targetETag, request, response);
			}
		}

		return handlePATCHRequestActions(targetURIObject, patchRequest, targetRDFSource, requestETag, application, request, response);
	}

	private ResponseEntity<Object> handleNonMatchinETags(URIObject targetURIObject, String requestETag, String targetETag, HttpServletRequest request,
			HttpServletResponse response) {
		String debugMessage = MessageFormat.format("The If-Match header didn''t match the document resource ETag. If-Match: {0}, ETag: {1}", requestETag,
				targetETag);

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handleNonMatchinETags() > {}", debugMessage);
		}

		ErrorResponseFactory factory = new ErrorResponseFactory();
		ErrorResponse errorObject = factory.create();
		errorObject.setHttpStatus(HttpStatus.PRECONDITION_FAILED);
		errorObject.setFriendlyMessage("The resource has been externally modified while processing the request. The request will be aborted.");
		errorObject.setDebugMessage(debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);
	}

	private ResponseEntity<Object> handlePATCHRequestActions(URIObject targetURIObject, PATCHRequest patchRequest, LDPRSource targetRDFSource,
			String requestETag, Application application, HttpServletRequest request, HttpServletResponse response) throws CarbonException {

		validatePATCHRequestActions(patchRequest);

		applyPATCHRequestActions(patchRequest, application);

		touchTargetRDFSource(targetURIObject, targetRDFSource);

		return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
	}

	private boolean patchRequestWasProvided(Resource resource) throws CarbonException {
		PATCHRequestFactory factory = new PATCHRequestFactory();
		try {
			return factory.isPATCHRequest(resource);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	protected PATCHRequest getPATCHRequest(Resource resource) throws CarbonException {
		PATCHRequest patchRequest = null;
		PATCHRequestFactory factory = new PATCHRequestFactory();
		try {
			patchRequest = factory.create(resource);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return patchRequest;
	}

	protected void validatePATCHRequest(PATCHRequest patchRequest) {
		// TODO Auto-generated method stub
	}

	protected LDPRSource getTargetRDFSource(URIObject targetURIObject, Application application) throws CarbonException {
		LDPRSource targetRDFSource = null;
		try {
			targetRDFSource = ldpService.getLDPRSource(targetURIObject, application.getDatasetName());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return targetRDFSource;
	}

	protected String getRequestETag(HttpServletRequest request) {
		return request.getHeader(HttpHeaders.IF_MATCH);
	}

	protected boolean eTagWasProvided(String requestETag) {
		return requestETag != null;
	}

	private String getTargetETag(LDPRSource targetRDFSource) {
		return targetRDFSource.getETag();
	}

	private void validatePATCHRequestActions(PATCHRequest patchRequest) {
		// TODO Auto-generated method stub

	}

	private void applyPATCHRequestActions(PATCHRequest patchRequest, Application application) throws CarbonException {
		Model model = rdfService.getNamedModel("http://carbonldp.com/apps/legacy/test", application.getDatasetName());
		if ( model.supportsTransactions() ) {
			model.begin();
			model.enterCriticalSection(Lock.WRITE);
			model.add(model.getResource("http://carbonldp.com/apps/legacy/test"), ResourceFactory.createProperty("http://worked.com/"), "Yeah it did");
			model.leaveCriticalSection();
			model.commit();
			model.close();
		}
	}

	private void touchTargetRDFSource(URIObject targetURIObject, LDPRSource targetRDFSource) {
		// TODO Auto-generated method stub

	}
}
