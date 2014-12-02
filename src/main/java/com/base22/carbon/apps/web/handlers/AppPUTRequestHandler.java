package com.base22.carbon.apps.web.handlers;

import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.Application;
import com.base22.carbon.apps.ApplicationClass.Properties;
import com.base22.carbon.apps.ApplicationRDF;
import com.base22.carbon.apps.ApplicationRDFFactory;
import com.base22.carbon.authorization.acl.ACLSystemResource;
import com.base22.carbon.authorization.acl.ACLSystemResourceFactory;
import com.base22.carbon.models.EmptyResponse;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class AppPUTRequestHandler extends AbstractAppRequestHandler {
	public ResponseEntity<Object> handleRequest(String appIdentifier, Model requestModel, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {

		ApplicationRDF requestRDFApplication = getRequestRDFApplication(requestModel, request);
		Application requestApplication = getRequestApplication(requestRDFApplication);

		Application targetApplication = getTargetApplication(appIdentifier);
		if ( ! targetApplicationExists(targetApplication) ) {
			return handleNonExistentApplication(appIdentifier, request, response);
		}

		// validateAppChanges(targetApplication, requestApplication);

		ACLSystemResource requestAclSR = requestRDFApplication.getAclSR();
		if ( requestAclSR != null ) {
			validateRequestAclSR(requestAclSR);
			applyRequestAclSR(targetApplication, requestAclSR);
		}

		// TODO: Apply Application changes

		return new ResponseEntity<Object>(new EmptyResponse(), HttpStatus.OK);
	}

	private ResponseEntity<Object> handleNonExistentApplication(String appIdentifier, HttpServletRequest request, HttpServletResponse response) {
		String friendlyMessage = "The application specified wasn't found.";
		String debugMessage = MessageFormat.format("The application with Identifier: ''{0}'', wasn''t found.", appIdentifier);

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("xx handleNonExistentApplication() > {}", debugMessage);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setHttpStatus(HttpStatus.NOT_FOUND);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);
	}

	private ApplicationRDF getRequestRDFApplication(Model requestModel, HttpServletRequest request) throws CarbonException {
		String applicationURI = HTTPUtil.getRequestURL(request);

		ApplicationRDF requestRDFApplication = null;
		ApplicationRDFFactory factory = new ApplicationRDFFactory();
		try {
			requestRDFApplication = factory.create(applicationURI, requestModel);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		return requestRDFApplication;
	}

	private Application getRequestApplication(ApplicationRDF requestRDFApplication) throws CarbonException {
		Application requestApplication = new Application();
		try {
			requestApplication.recoverFromLDPR(requestRDFApplication);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return requestApplication;
	}

	private Application getTargetApplication(String appIdentifier) throws CarbonException {
		Application targetApplication = null;
		try {
			targetApplication = securedApplicationDAO.findByIdentifier(appIdentifier);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return targetApplication;
	}

	private boolean targetApplicationExists(Application targetApplication) {
		return targetApplication != null;
	}

	private void validateAppChanges(Application targetApplication, Application requestApplication) throws CarbonException {
		StringBuilder bodyIssueBuilder = null;

		if ( requestApplication.getUuid() != null && (! targetApplication.getUuid().equals(requestApplication.getUuid())) ) {
			bodyIssueBuilder = new StringBuilder();

			bodyIssueBuilder.append(Properties.UUID.getPrefixedURI().getSlug());
			bodyIssueBuilder.append(" > Cannot be changed.");
		}

		if ( ! requestApplication.getSlug().equals(targetApplication.getSlug()) ) {
			if ( targetApplication != null ) {
				bodyIssueBuilder = new StringBuilder();

				bodyIssueBuilder.append(Properties.SLUG.getPrefixedURI().getSlug());
				bodyIssueBuilder.append(" > Cannot be changed once it has been set.");
			}
		}

		if ( requestApplication.getName() == null ) {
			if ( bodyIssueBuilder == null ) {
				bodyIssueBuilder = new StringBuilder();
			} else {
				bodyIssueBuilder.append("\n");
			}

			bodyIssueBuilder.append(Properties.NAME.getPrefixedURI().getSlug());
			bodyIssueBuilder.append(" > Required.");
		}

		if ( requestApplication.getMasterKey() != null && (! targetApplication.getMasterKey().equals(requestApplication.getMasterKey())) ) {
			bodyIssueBuilder = new StringBuilder();

			bodyIssueBuilder.append(Properties.MASTER_KEY.getPrefixedURI().getSlug());
			bodyIssueBuilder.append(" > Cannot be changed.");
		}

		if ( bodyIssueBuilder != null ) {
			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = "The properties of the Application sent are not valid.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< validateAppChanges() > {}", debugMessage);
			}

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			errorObject.setEntityBodyIssue(null, bodyIssueBuilder.toString());

			throw new CarbonException(errorObject);
		}
	}

	private void validateRequestAclSR(ACLSystemResource requestAclSR) throws CarbonException {
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

	private void applyRequestAclSR(Application targetApplication, ACLSystemResource requestAclSR) throws CarbonException {
		try {
			ldpPermissionService.replaceLDPResourceACL(targetApplication, requestAclSR);
		} catch (CarbonException e) {
			if ( e.getErrorObject().getHttpStatus() == null ) {
				e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			throw e;
		}
	}
}
