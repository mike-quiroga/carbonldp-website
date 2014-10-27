package com.base22.carbon.authorization;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.Application;
import com.base22.carbon.authentication.AgentAuthenticationToken;
import com.base22.carbon.authentication.ApplicationContextToken;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;

public abstract class AuthorizationUtil {

	protected static final Logger LOG = LoggerFactory.getLogger(AuthorizationUtil.class);

	public static Application getApplicationFromContext() throws CarbonException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( authentication == null ) {
			return null;
		}
		if ( ! (authentication instanceof ApplicationContextToken) ) {
			String friendlyMessage = "There was a problem processing your request. Please contact an administrator.";
			String debugMessage = "The application context was never set. Can't proceed with the request.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< getApplicationFromContext() > {}", debugMessage);
			}

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);

			throw new CarbonException(errorObject);
		}
		Application application = ((ApplicationContextToken) authentication).getCurrentApplicationContext();
		return application;
	}

	public static UUID getAgentUUIDFromContext() throws CarbonException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( authentication == null ) {
			return null;
		}
		if ( ! (authentication instanceof AgentAuthenticationToken) ) {
			String friendlyMessage = "There was a problem processing your request. Please contact an administrator.";
			String debugMessage = "The context was not properly set. Can't proceed with the request.";

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("<< getAgentUUIDFromContext() > {}", debugMessage);
			}

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);

			throw new CarbonException(errorObject);
		}
		return ((AgentAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getPrincipal().getUuid();
	}
}