package com.base22.carbon.apps.web.handlers;

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
import com.base22.carbon.HTTPHeaders;
import com.base22.carbon.apps.Application;
import com.base22.carbon.apps.ApplicationRDF;
import com.base22.carbon.authorization.acl.AclSR;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.models.HttpHeader;
import com.base22.carbon.models.HttpHeaderValue;
import com.base22.carbon.utils.HTTPUtil;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class AppGETRequestHandler extends AbstractAppRequestHandler {
	public ResponseEntity<Object> handleRequest(String appIdentifier, HttpServletRequest request, HttpServletResponse response) throws CarbonException {

		Enumeration<String> preferHeaders = request.getHeaders(HTTPHeaders.PREFER);
		HttpHeader preferHeader = new HttpHeader(preferHeaders);

		Application targetApplication = getTargetApplication(appIdentifier);
		if ( ! targetApplicationExists(targetApplication) ) {
			return handleNonExistentApplication(appIdentifier, request, response);
		}

		ApplicationRDF targetRDFApplication = targetApplication.createRDFRepresentation();

		if ( includeACL(preferHeader) ) {
			try {
				ldpPermissionService.injectACLToLDPResource(targetApplication, targetRDFApplication);
			} catch (CarbonException e) {
				return HTTPUtil.createErrorResponseEntity(e, HttpStatus.INTERNAL_SERVER_ERROR);
			}

			HttpHeaderValue aclPreference = new HttpHeaderValue();
			aclPreference.setMainKey("return");
			aclPreference.setMainValue("representation");
			aclPreference.setExtendingKey("include");
			aclPreference.setExtendingValue(AclSR.Resources.CLASS.getPrefixedURI().getURI());

			response.addHeader("Preference-Applied", aclPreference.toString());
		}

		return new ResponseEntity<Object>(targetRDFApplication, HttpStatus.OK);
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

}
