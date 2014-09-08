package com.base22.carbon.apps.roles.web.handlers;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.Application;
import com.base22.carbon.apps.roles.ApplicationRole;
import com.base22.carbon.apps.web.handlers.AbstractAppRequestHandler;
import com.base22.carbon.authorization.AuthorizationUtil;
import com.base22.carbon.ldp.models.LDPResource;
import com.base22.carbon.ldp.models.LDPResourceFactory;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class RolesPOSTRequestHandler extends AbstractAppRequestHandler {

	public ResponseEntity<Object> handleRequest(String appSlug, Model requestModel, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {

		Application application = AuthorizationUtil.getApplicationFromContext();

		Resource requestResource = getRequestModelMainResource(requestModel);
		ApplicationRole appRole = getRequestApplicationRole(requestResource);
		appRole.setApplicationSlug(appSlug);

		validateApplicationRole(appRole);

		if ( slugWasProvided(appRole) ) {
			sluggifyProvidedSlug(appRole);
		} else {
			createAppRoleSlug(appRole);
		}
		if ( slugIsAlreadyInUse(appRole, appSlug) ) {
			return handleSlugAlreadyRegistred(appRole, request, response);
		}

		ApplicationRole parentRole = getParentApplicationRole(appRole.getParentSlug(), appSlug);

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

	private ResponseEntity<Object> handleSlugAlreadyRegistred(ApplicationRole appRole, HttpServletRequest request, HttpServletResponse response) {
		String friendlyMessage = "The slug provided is already in use.";
		String debugMessage = MessageFormat.format("The slug provided is already in use.", appRole.getSlug());

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handleSlugAlreadyRegistred() > {}", debugMessage);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setHttpStatus(HttpStatus.CONFLICT);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);
	}

	private boolean slugWasProvided(ApplicationRole appRole) {
		return appRole.getSlug() != null;
	}

	private void sluggifyProvidedSlug(ApplicationRole appRole) {
		appRole.setSlug(HTTPUtil.createSlug(appRole.getSlug()));
	}

	private boolean slugIsAlreadyInUse(ApplicationRole appRole, String appSlug) throws CarbonException {
		try {
			return unsecuredApplicationRoleDAO.findBySlug(appRole.getSlug(), appSlug) != null;
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private void createAppRoleSlug(ApplicationRole appRole) {
		appRole.setSlug(HTTPUtil.createSlug(appRole.getName()));
	}

	private ResponseEntity<Object> handleNonExistentParent(ApplicationRole appRole, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {
		String friendlyMessage = "The parent role specified wasn't found.";
		String debugMessage = MessageFormat.format("The parent role with Slug: ''{0}'', wasn''t found.", appRole.getParentSlug());

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("xx handleNonExistentParent() > {}", debugMessage);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setHttpStatus(HttpStatus.NOT_FOUND);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);
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

		return HTTPUtil.createErrorResponseEntity(errorObject);
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

		if ( applicationRole.getParentSlug() == null ) {
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

	protected ApplicationRole getParentApplicationRole(String string, String appSlug) throws CarbonException {
		ApplicationRole parentRole = null;
		try {
			parentRole = unsecuredApplicationRoleDAO.findBySlug(string, appSlug);
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
