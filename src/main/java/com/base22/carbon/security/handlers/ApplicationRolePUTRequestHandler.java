package com.base22.carbon.security.handlers;

import java.text.MessageFormat;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.security.dao.ApplicationRoleDAO;
import com.base22.carbon.security.models.ACLSystemResource;
import com.base22.carbon.security.models.ACLSystemResourceFactory;
import com.base22.carbon.security.models.ApplicationRole;
import com.base22.carbon.security.models.ApplicationRole.Properties;
import com.base22.carbon.security.models.RDFApplicationRole;
import com.base22.carbon.security.models.RDFApplicationRoleFactory;
import com.base22.carbon.security.services.LDPPermissionService;
import com.base22.carbon.security.services.PermissionService;
import com.base22.carbon.security.utils.AuthenticationUtil;
import com.base22.carbon.services.ConfigurationService;
import com.base22.carbon.utils.HttpUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class ApplicationRolePUTRequestHandler {

	@Autowired
	private ApplicationRoleDAO applicationRoleDAO;
	@Autowired
	private ConfigurationService configurationService;

	@Autowired
	protected LDPPermissionService ldpPermissionService;
	@Autowired
	private PermissionService permissionService;

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	public ResponseEntity<Object> replaceApplicationRole(String appIdentifier, String targetAppRoleUUID, Model requestModel, HttpServletRequest request,
			HttpServletResponse response) throws CarbonException {

		RDFApplicationRole requestRDFAppRole = getRequestRDFApplicationRole(requestModel, request);
		ApplicationRole requestAppRole = getRequestApplicationRole(requestRDFAppRole);
		ApplicationRole targetAppRole = getTargetApplicationRole(targetAppRoleUUID);

		if ( ! targetAppRoleExists(targetAppRole) ) {
			return handleNonExistentAppRole(targetAppRoleUUID, request, response);
		}

		validateAppRoleChanges(targetAppRole, requestAppRole);

		ACLSystemResource requestAclSR = requestRDFAppRole.getAclSR();
		if ( requestAclSR != null ) {
			validateRequestAclSR(requestAclSR);
			applyRequestAclSR(targetAppRole, requestAclSR);
		}

		// TODO: Apply ApplicationRole changes

		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	private ResponseEntity<Object> handleNonExistentAppRole(String targetAppRoleUUID, HttpServletRequest request, HttpServletResponse response) {
		String friendlyMessage = "The application role specified wasn't found.";
		String debugMessage = MessageFormat.format("The application role with UUID: ''{0}'', wasn''t found.", targetAppRoleUUID);

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("xx handleNonExistentAppRole() > {}", debugMessage);
		}

		ErrorResponse errorObject = new ErrorResponse();
		errorObject.setHttpStatus(HttpStatus.NOT_FOUND);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HttpUtil.createErrorResponseEntity(errorObject);
	}

	private ApplicationRole getTargetApplicationRole(String uuidString) throws CarbonException {
		if ( ! AuthenticationUtil.isUUIDString(uuidString) ) {
			String friendlyMessage = "The request URL doesn't a valid UUID for the application role that will be modified.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx getRequestApplicationRole() > {}", friendlyMessage);
			}

			ErrorResponse errorObject = new ErrorResponse();
			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);

			throw new CarbonException(errorObject);
		}
		UUID targetAppRoleUUID = AuthenticationUtil.restoreUUID(uuidString);

		ApplicationRole targetAppRole = null;
		try {
			targetAppRole = applicationRoleDAO.findByUUID(targetAppRoleUUID);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return targetAppRole;
	}

	private ApplicationRole getRequestApplicationRole(RDFApplicationRole requestRDFAppRole) throws CarbonException {
		ApplicationRole requestAppRole = new ApplicationRole();
		requestAppRole.recoverFromLDPR(requestRDFAppRole);
		return requestAppRole;
	}

	private RDFApplicationRole getRequestRDFApplicationRole(Model requestModel, HttpServletRequest request) throws CarbonException {
		String appRoleURI = HttpUtil.getRequestURL(request);

		RDFApplicationRole requestRDFAppRole = null;
		RDFApplicationRoleFactory factory = new RDFApplicationRoleFactory();
		try {
			requestRDFAppRole = factory.create(appRoleURI, requestModel);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		return requestRDFAppRole;
	}

	private boolean targetAppRoleExists(ApplicationRole targetAppRole) {
		return targetAppRole != null;
	}

	private void validateAppRoleChanges(ApplicationRole targetAppRole, ApplicationRole requestAppRole) throws CarbonException {
		StringBuilder bodyIssueBuilder = null;

		if ( requestAppRole.getUuid() != targetAppRole.getUuid() ) {
			bodyIssueBuilder = new StringBuilder();

			bodyIssueBuilder.append(Properties.UUID.getPrefixedURI().getSlug());
			bodyIssueBuilder.append(" > Cannot be changed.");
		}

		if ( requestAppRole.getApplicationUUID() != targetAppRole.getApplicationUUID() ) {
			if ( bodyIssueBuilder == null ) {
				bodyIssueBuilder = new StringBuilder();
			} else {
				bodyIssueBuilder.append("\n");
			}

			bodyIssueBuilder.append(Properties.APPLICATION.getPrefixedURI().getSlug());
			bodyIssueBuilder.append(" > Cannot be changed.");
		}

		if ( requestAppRole.getParentUUID() != targetAppRole.getApplicationUUID() ) {
			if ( bodyIssueBuilder == null ) {
				bodyIssueBuilder = new StringBuilder();
			} else {
				bodyIssueBuilder.append("\n");
			}

			bodyIssueBuilder.append(Properties.PARENT.getPrefixedURI().getSlug());
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

			ErrorResponse errorObject = new ErrorResponse();
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

			ErrorResponse errorObject = new ErrorResponse();
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
