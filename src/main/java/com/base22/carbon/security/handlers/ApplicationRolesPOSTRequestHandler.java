package com.base22.carbon.security.handlers;

import java.text.MessageFormat;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.models.LDPResource;
import com.base22.carbon.models.LDPResourceFactory;
import com.base22.carbon.security.models.Application;
import com.base22.carbon.security.models.ApplicationRole;
import com.base22.carbon.security.utils.AuthorizationUtil;
import com.base22.carbon.utils.HttpUtil;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class ApplicationRolesPOSTRequestHandler extends AbstractApplicationAPIRequestHandler {

	public ResponseEntity<Object> handleRequest(String appIdentifier, Model requestModel, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {

		Application application = AuthorizationUtil.getApplicationFromContext();

		Resource requestResource = getRequestModelMainResource(requestModel);
		ApplicationRole appRole = getRequestApplicationRole(requestResource);

		validateApplicationRole(appRole);

		ApplicationRole parentRole = getParentApplicationRole(appRole.getParentUUID());

		if ( ! parentRoleExists(parentRole) ) {
			return handleNonExistentParent(appRole, request, response);
		}

		if ( ! parentRoleBelongsToApplication(parentRole, application) ) {
			return handleParentOutsideOfApplication(parentRole, application, request, response);
		}

		appRole = createApplicationRole(appRole, parentRole);

		setParentACLToApplicationRole(appRole, parentRole);

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< createApplicationRole() > A child of the applicationRole: '{}', has been created.", parentRole.getUuid().toString());
		}

		return new ResponseEntity<Object>(appRole.createRDFRepresentation(), HttpStatus.CREATED);
	}

	private ResponseEntity<Object> handleNonExistentParent(ApplicationRole appRole, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {
		String friendlyMessage = "The parent role specified wasn't found.";
		String debugMessage = MessageFormat.format("The parent role with UUID: ''{0}'', wasn''t found.", appRole.getParentUUID().toString());

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("xx handleNonExistentParent() > {}", debugMessage);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setHttpStatus(HttpStatus.NOT_FOUND);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HttpUtil.createErrorResponseEntity(errorObject);
	}

	private ResponseEntity<Object> handleParentOutsideOfApplication(ApplicationRole parentRole, Application application, HttpServletRequest request,
			HttpServletResponse response) {
		String friendlyMessage = "The parent role of the application role doesn't belong to this application.";
		String debugMessage = MessageFormat.format("The parent role with UUID: ''{0}'', doesn't belong to the application with UUID: ''{1}''.",
				parentRole.getUuidString(), application.getUuidString());

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("xx handleParentOutsideOfApplication() > {}", debugMessage);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setHttpStatus(HttpStatus.NOT_FOUND);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HttpUtil.createErrorResponseEntity(errorObject);
	}

	protected ApplicationRole getRequestApplicationRole(Resource resource) throws CarbonException {
		LDPResourceFactory factory = new LDPResourceFactory();
		LDPResource ldpResource = null;
		try {
			ldpResource = factory.create(resource);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		ApplicationRole appRole = new ApplicationRole();
		try {
			appRole.recoverFromLDPR(ldpResource);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return appRole;
	}

	// TODO: Validate other properties that are not required or will be ignored
	protected void validateApplicationRole(ApplicationRole applicationRole) throws CarbonException {
		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = null;

		if ( applicationRole.getParentUUID() == null ) {
			if ( errorObject == null ) {
				errorObject = errorFactory.create();
			}

			errorObject.addParameterIssue(ApplicationRole.Properties.PARENT.getPrefixedURI().getSlug(), null, "required", null);
		}
		if ( applicationRole.getName() == null ) {
			if ( errorObject == null ) {
				errorObject = errorFactory.create();
			}

			errorObject.addParameterIssue(ApplicationRole.Properties.NAME.getPrefixedURI().getSlug(), null, "required", null);
		}

		if ( errorObject != null ) {
			String friendlyMessage = "The applicationRole sent isn't valid.";
			String debugMessage = "The applicationRole is missing required properties.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx validateApplicationRole() > {}", debugMessage);
			}

			errorObject.setHttpStatus(HttpStatus.BAD_REQUEST);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);

			throw new CarbonException(errorObject);
		}
	}

	protected ApplicationRole getParentApplicationRole(UUID parentUUID) throws CarbonException {
		ApplicationRole parentRole = null;
		try {
			parentRole = unsecuredApplicationRoleDAO.findByUUID(parentUUID);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		return parentRole;
	}

	protected boolean parentRoleExists(ApplicationRole parentRole) {
		return parentRole != null;
	}

	private boolean parentRoleBelongsToApplication(ApplicationRole parentRole, Application application) {
		return parentRole.getApplicationUUID().equals(application.getUuid());
	}

	private ApplicationRole createApplicationRole(ApplicationRole appRole, ApplicationRole parentRole) throws CarbonException {
		ApplicationRole childRole = null;
		try {
			childRole = unsecuredApplicationRoleDAO.createChildApplicationRole(parentRole, appRole);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return childRole;
	}

	private void setParentACLToApplicationRole(ApplicationRole appRole, ApplicationRole parentRole) throws CarbonException {
		try {
			permissionService.setParent(appRole, parentRole);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}
}
