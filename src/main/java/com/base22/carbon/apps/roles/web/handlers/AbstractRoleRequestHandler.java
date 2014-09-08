package com.base22.carbon.apps.roles.web.handlers;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.roles.ApplicationRole;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.utils.HTTPUtil;
import com.base22.carbon.web.AbstractRequestHandler;

public class AbstractRoleRequestHandler extends AbstractRequestHandler {

	protected ResponseEntity<Object> handleNonExistentAppRole(String appRoleSlug, HttpServletRequest request, HttpServletResponse response) {
		String friendlyMessage = "The application role specified wasn't found.";
		String debugMessage = MessageFormat.format("The application role with Slug: ''{0}'', wasn''t found.", appRoleSlug);

		if ( LOG.isDebugEnabled() ) {
			LOG.debug("xx handleNonExistentAppRole() > {}", debugMessage);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setHttpStatus(HttpStatus.NOT_FOUND);
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);

		return HTTPUtil.createErrorResponseEntity(errorObject);
	}

	protected ApplicationRole getTargetApplicationRole(String appRoleSlug, String appSlug) throws CarbonException {
		ApplicationRole targetAppRole = null;
		try {
			targetAppRole = securedApplicationRoleDAO.findBySlug(appRoleSlug, appSlug);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return targetAppRole;
	}

	protected boolean targetAppRoleExists(ApplicationRole targetAppRole) {
		return targetAppRole != null;
	}

}
