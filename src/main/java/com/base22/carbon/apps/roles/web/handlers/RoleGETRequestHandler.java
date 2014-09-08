package com.base22.carbon.apps.roles.web.handlers;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.List;

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
import com.base22.carbon.apps.roles.ApplicationRole;
import com.base22.carbon.apps.roles.ApplicationRoleRDF;
import com.base22.carbon.apps.web.handlers.AbstractAppRequestHandler;
import com.base22.carbon.authorization.acl.AclSR;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.models.HttpHeader;
import com.base22.carbon.models.HttpHeaderValue;
import com.base22.carbon.utils.HTTPUtil;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class RoleGETRequestHandler extends AbstractAppRequestHandler {

	public ResponseEntity<Object> handleRequest(String appSlug, String appRoleSlug, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {

		ApplicationRole targetAppRole = getTargetAppRole(appSlug, appRoleSlug);
		if ( ! targetAppRoleExists(targetAppRole) ) {
			return handleNonExistentAppRole(appSlug, appRoleSlug, request, response);
		}

		ApplicationRoleRDF targetRDFAppRole = targetAppRole.createRDFRepresentation();

		Enumeration<String> preferHeaders = request.getHeaders(HttpHeaders.PREFER);
		HttpHeader preferHeader = new HttpHeader(preferHeaders);
		if ( includeACL(preferHeader) ) {
			injectACLToTargetAppRole(targetAppRole, targetRDFAppRole, response);
		}

		return new ResponseEntity<Object>(targetRDFAppRole, HttpStatus.OK);
	}

	private ResponseEntity<Object> handleNonExistentAppRole(String appSlug, String appRoleSlug, HttpServletRequest request, HttpServletResponse response) {
		String friendlyMessage = "The applicationRole specified wasn't found.";
		String debugMessage = MessageFormat.format("The applicationRole with the slug: ''{0}'', wasn''t found.", appRoleSlug);

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

	private ApplicationRole getTargetAppRole(String appIdentifier, String appRoleSlug) throws CarbonException {
		Application targetApplication = null;
		try {
			targetApplication = securedApplicationDAO.findByIdentifier(appIdentifier);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		if ( targetApplication == null ) {
			String friendlyMessage = "The application specified wasn't found.";
			String debugMessage = MessageFormat.format("The application with the Identifier: ''{0}'', wasn''t found.", appIdentifier);

			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx handleNonExistentAppRole() > {}", debugMessage);
			}

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setHttpStatus(HttpStatus.NOT_FOUND);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);

			throw new CarbonException(errorObject);
		}

		ApplicationRole targetAppRole = null;
		try {
			targetAppRole = securedApplicationRoleDAO.findBySlug(appRoleSlug, appIdentifier);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return targetAppRole;
	}

	private boolean targetAppRoleExists(ApplicationRole targetAppRole) {
		return targetAppRole != null;
	}

	private boolean includeACL(HttpHeader preferHeader) {
		boolean include = false;
		List<HttpHeaderValue> includePreferences = HttpHeader.filterHeaderValues(preferHeader, "return", "representation", "include", null);

		for (HttpHeaderValue includePreference : includePreferences) {
			String includeValue = includePreference.getExtendingValue();
			if ( includeValue != null ) {
				if ( AclSR.Resources.findByURI(includeValue) == AclSR.Resources.CLASS ) {
					include = true;
				}
			}
		}
		return include;
	}

	private void injectACLToTargetAppRole(ApplicationRole targetAppRole, ApplicationRoleRDF targetRDFAppRole, HttpServletResponse response)
			throws CarbonException {
		try {
			ldpPermissionService.injectACLToLDPResource(targetAppRole, targetRDFAppRole);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}

		HttpHeaderValue aclPreference = new HttpHeaderValue();
		aclPreference.setMainKey("return");
		aclPreference.setMainValue("representation");
		aclPreference.setExtendingKey("include");
		aclPreference.setExtendingValue(AclSR.Resources.CLASS.getPrefixedURI().getURI());

		response.addHeader("Preference-Applied", aclPreference.toString());
	}
}
