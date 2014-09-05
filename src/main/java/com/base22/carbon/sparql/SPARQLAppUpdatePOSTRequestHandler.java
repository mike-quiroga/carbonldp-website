package com.base22.carbon.sparql;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.Application;
import com.base22.carbon.web.AbstractRequestHandler;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class SPARQLAppUpdatePOSTRequestHandler extends AbstractRequestHandler {
	public ResponseEntity<Object> handleRequest(String appIdentifier, String queryString, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {

		Application targetApplication = getTargetApplication(appIdentifier);
		if ( ! targetApplicationExists(targetApplication) ) {
			return handleNonExistentApplication(appIdentifier, request, response);
		}

		this.executeSPARQLUpdate(queryString, targetApplication);

		// addHeadersToResponse(response, targetURIObject);

		return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
	}

	private ResponseEntity<Object> handleNonExistentApplication(String appIdentifier, HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return null;
	}

	private Application getTargetApplication(String appIdentifier) {
		// TODO Auto-generated method stub
		return null;
	}

	private boolean targetApplicationExists(Application targetApplication) {
		// TODO Auto-generated method stub
		return false;
	}

	private void executeSPARQLUpdate(String queryString, Application targetApplication) {
		// TODO Auto-generated method stub

	}

}
