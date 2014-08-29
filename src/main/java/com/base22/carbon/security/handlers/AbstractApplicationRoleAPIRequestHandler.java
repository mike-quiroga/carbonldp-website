package com.base22.carbon.security.handlers;

import java.text.MessageFormat;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.security.models.ApplicationRole;
import com.base22.carbon.security.utils.AuthenticationUtil;
import com.base22.carbon.utils.HttpUtil;

public class AbstractApplicationRoleAPIRequestHandler extends AbstractAPIRequestHandler {

	protected ResponseEntity<Object> handleNonExistentAppRole(String targetAppRoleUUID, HttpServletRequest request, HttpServletResponse response) {
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

	protected ApplicationRole getTargetApplicationRole(String uuidString) throws CarbonException {
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
			targetAppRole = securedApplicationRoleDAO.findByUUID(targetAppRoleUUID);
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
