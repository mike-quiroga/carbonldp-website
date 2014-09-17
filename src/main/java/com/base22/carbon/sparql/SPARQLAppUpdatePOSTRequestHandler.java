package com.base22.carbon.sparql;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.base22.carbon.APIPreferences.InteractionModel;
import com.base22.carbon.CarbonException;
import com.base22.carbon.HTTPHeaders;
import com.base22.carbon.apps.Application;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.models.HttpHeaderValue;
import com.base22.carbon.utils.HTTPUtil;
import com.base22.carbon.web.AbstractRequestHandler;
import com.hp.hpl.jena.update.UpdateRequest;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class SPARQLAppUpdatePOSTRequestHandler extends AbstractRequestHandler {
	public ResponseEntity<Object> handleRequest(String appIdentifier, String updateString, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {

		Application targetApplication = getTargetApplication(appIdentifier);
		if ( ! targetApplicationExists(targetApplication) ) {
			return handleNonExistentApplication(appIdentifier, request, response);
		}

		updateString = SPARQLUtil.setDefaultNSPrefixes(updateString, true);

		UpdateRequest updateRequest = this.getUpdateRequest(updateString);

		this.executeSPARQLUpdate(updateRequest, targetApplication);

		addHeadersToResponse(response, targetApplication);

		return new ResponseEntity<Object>(HttpStatus.OK);
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

	private UpdateRequest getUpdateRequest(String updateString) throws CarbonException {
		UpdateRequest updateRequest = null;
		try {
			updateRequest = sparqlService.createUpdateRequest(updateString, true);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.BAD_REQUEST);
			throw e;
		}
		return updateRequest;
	}

	private void executeSPARQLUpdate(UpdateRequest updateRequest, Application targetApplication) throws CarbonException {
		try {
			sparqlService.update(targetApplication, updateRequest);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
	}

	private void addHeadersToResponse(HttpServletResponse response, Application targetApplication) {
		addLinkHeader(response);
		// TODO: Add location header
	}

	private void addLinkHeader(HttpServletResponse response) {
		HttpHeaderValue linkHeader = new HttpHeaderValue();
		linkHeader.setMainValue(InteractionModel.SPARQL_ENDPOINT.getPrefixedURI().getResourceURI());
		linkHeader.setExtendingKey("rel");
		linkHeader.setExtendingValue("type");

		response.addHeader(HTTPHeaders.LINK, linkHeader.toString());
	}
}
