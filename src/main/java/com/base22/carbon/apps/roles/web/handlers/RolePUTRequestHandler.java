package com.base22.carbon.apps.roles.web.handlers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.roles.ApplicationRole;
import com.base22.carbon.apps.roles.ApplicationRole.Properties;
import com.base22.carbon.apps.roles.ApplicationRoleRDF;
import com.base22.carbon.apps.roles.ApplicationRoleRDFFactory;
import com.base22.carbon.authorization.acl.ACLSystemResource;
import com.base22.carbon.authorization.acl.ACLSystemResourceFactory;
import com.base22.carbon.models.EmptyResponse;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class RolePUTRequestHandler extends AbstractRoleRequestHandler {

	public ResponseEntity<Object> replaceApplicationRole(String appSlug, String appRoleSlug, Model requestModel, HttpServletRequest request,
			HttpServletResponse response) throws CarbonException {

		ApplicationRoleRDF requestRDFAppRole = getRequestRDFApplicationRole(requestModel, request);
		ApplicationRole requestAppRole = getRequestApplicationRole(requestRDFAppRole);

		// TODO: Take into account the application we are in
		ApplicationRole targetAppRole = getTargetApplicationRole(appRoleSlug, appSlug);

		if ( ! targetAppRoleExists(targetAppRole) ) {
			return handleNonExistentAppRole(appRoleSlug, request, response);
		}

		validateAppRoleChanges(targetAppRole, requestAppRole);

		ACLSystemResource requestAclSR = requestRDFAppRole.getAclSR();
		if ( requestAclSR != null ) {
			validateRequestAclSR(requestAclSR);
			applyRequestAclSR(targetAppRole, requestAclSR);
		}

		// TODO: Apply ApplicationRole changes

		return new ResponseEntity<Object>(new EmptyResponse(), HttpStatus.OK);
	}

	private ApplicationRole getRequestApplicationRole(ApplicationRoleRDF requestRDFAppRole) throws CarbonException {
		ApplicationRole requestAppRole = new ApplicationRole();
		requestAppRole.recoverFromLDPR(requestRDFAppRole);
		return requestAppRole;
	}

	private ApplicationRoleRDF getRequestRDFApplicationRole(Model requestModel, HttpServletRequest request) throws CarbonException {
		String appRoleURI = HTTPUtil.getRequestURL(request);

		ApplicationRoleRDF requestRDFAppRole = null;
		ApplicationRoleRDFFactory factory = new ApplicationRoleRDFFactory();
		try {
			requestRDFAppRole = factory.create(appRoleURI, requestModel);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		return requestRDFAppRole;
	}

	private void validateAppRoleChanges(ApplicationRole targetAppRole, ApplicationRole requestAppRole) throws CarbonException {
		StringBuilder bodyIssueBuilder = null;

		if ( requestAppRole.getUuid() != targetAppRole.getUuid() ) {
			bodyIssueBuilder = new StringBuilder();

			bodyIssueBuilder.append(Properties.UUID.getPrefixedURI().getSlug());
			bodyIssueBuilder.append(" > Cannot be changed.");
		}

		if ( ! requestAppRole.getApplicationSlug().equals(targetAppRole.getApplicationSlug()) ) {
			if ( bodyIssueBuilder == null ) {
				bodyIssueBuilder = new StringBuilder();
			} else {
				bodyIssueBuilder.append("\n");
			}

			bodyIssueBuilder.append(Properties.APPLICATION.getPrefixedURI().getSlug());
			bodyIssueBuilder.append(" > Cannot be changed.");
		}

		if ( targetAppRole.getParentSlug() == null ) {
			if ( requestAppRole.getParentSlug() != null ) {
				if ( bodyIssueBuilder == null ) {
					bodyIssueBuilder = new StringBuilder();
				} else {
					bodyIssueBuilder.append("\n");
				}

				bodyIssueBuilder.append(Properties.PARENT.getPrefixedURI().getSlug());
				bodyIssueBuilder.append(" > Cannot be changed.");
			}
		} else {
			if ( requestAppRole.getParentSlug() == null ) {
				if ( bodyIssueBuilder == null ) {
					bodyIssueBuilder = new StringBuilder();
				} else {
					bodyIssueBuilder.append("\n");
				}

				bodyIssueBuilder.append(Properties.PARENT.getPrefixedURI().getSlug());
				bodyIssueBuilder.append(" > Cannot be changed.");

			} else if ( ! requestAppRole.getParentSlug().equals(targetAppRole.getParentSlug()) ) {
				if ( bodyIssueBuilder == null ) {
					bodyIssueBuilder = new StringBuilder();
				} else {
					bodyIssueBuilder.append("\n");
				}

				bodyIssueBuilder.append(Properties.PARENT.getPrefixedURI().getSlug());
				bodyIssueBuilder.append(" > Cannot be changed.");

			}
		}

		if ( requestAppRole.getSlug() == null ) {
			if ( bodyIssueBuilder == null ) {
				bodyIssueBuilder = new StringBuilder();
			} else {
				bodyIssueBuilder.append("\n");
			}

			bodyIssueBuilder.append(Properties.SLUG.getPrefixedURI().getSlug());
			bodyIssueBuilder.append(" > Cannot be changed.");
		} else if ( ! requestAppRole.getSlug().equals(targetAppRole.getSlug()) ) {
			if ( bodyIssueBuilder == null ) {
				bodyIssueBuilder = new StringBuilder();
			} else {
				bodyIssueBuilder.append("\n");
			}

			bodyIssueBuilder.append(Properties.SLUG.getPrefixedURI().getSlug());
			bodyIssueBuilder.append(" > Cannot be changed.");

		}

		if ( requestAppRole.getName() == null ) {
			if ( bodyIssueBuilder == null ) {
				bodyIssueBuilder = new StringBuilder();
			} else {
				bodyIssueBuilder.append("\n");
			}

			bodyIssueBuilder.append(Properties.NAME.getPrefixedURI().getSlug());
			bodyIssueBuilder.append(" > Required.");
		}

		if ( bodyIssueBuilder != null ) {
			String friendlyMessage = "The body of the request is not valid.";
			String debugMessage = "The properties of the ApplicationRole sent are not valid.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< validateAppRoleChanges() > {}", debugMessage);
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

	private void applyRequestAclSR(ApplicationRole targetAppRole, ACLSystemResource requestAclSR) throws CarbonException {
		try {
			ldpPermissionService.replaceLDPResourceACL(targetAppRole, requestAclSR);
		} catch (CarbonException e) {
			if ( e.getErrorObject().getHttpStatus() == null ) {
				e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			}
			throw e;
		}
	}

}
