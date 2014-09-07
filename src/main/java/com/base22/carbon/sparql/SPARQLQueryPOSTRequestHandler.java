package com.base22.carbon.sparql;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.base22.carbon.APIPreferences.InteractionModel;
import com.base22.carbon.CarbonException;
import com.base22.carbon.HttpHeaders;
import com.base22.carbon.apps.Application;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.models.HttpHeaderValue;
import com.base22.carbon.sparql.SPARQLService.Verb;
import com.base22.carbon.utils.HTTPUtil;
import com.base22.carbon.web.AbstractRequestHandler;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "request")
public class SPARQLQueryPOSTRequestHandler extends AbstractRequestHandler {

	public ResponseEntity<Object> handleRequest(String applicationIdentifier, String queryString, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {

		Application application = getApplicationFromContext();

		String targetURI = getTargetURI(request);
		URIObject targetURIObject = getTargetURIObject(targetURI);

		if ( targetSourceExists(targetURIObject) ) {
			return handleNonExistentSource(targetURI, request, response);
		}

		queryString = SPARQLUtil.setDefaultNSPrefixes(queryString, true);

		Query query = composeSPARQLQuery(queryString);

		Object result = executeSPARQLQuery(query, targetURIObject, application);

		addHeadersToResponse(response, targetURIObject);

		return new ResponseEntity<Object>(result, HttpStatus.OK);
	}

	private ResponseEntity<Object> handleNonExistentSource(String targetURI, HttpServletRequest request, HttpServletResponse response) {
		String friendlyMessage = "The document specified wasn't found.";
		String debugMessage = MessageFormat.format("The document with URI: ''{0}'', wasn''t found.", targetURI);

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

	private boolean targetSourceExists(URIObject targetURIObject) {
		// TODO Auto-generated method stub
		return false;
	}

	private String getTargetURI(HttpServletRequest request) {
		return HTTPUtil.getRequestURL(request);
	}

	private URIObject getTargetURIObject(String targetURI) throws CarbonException {
		URIObject targetURIObject = null;
		try {
			targetURIObject = uriObjectDAO.findByURI(targetURI);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		} catch (AccessDeniedException e) {
			String friendlyMessage = "The document specified wasn't found.";
			String debugMessage = MessageFormat.format("The document with URI: ''{0}'', wasn''t found.", targetURI);

			ErrorResponseFactory errorFactory = new ErrorResponseFactory();
			ErrorResponse errorObject = errorFactory.create();
			errorObject.setHttpStatus(HttpStatus.NOT_FOUND);
			errorObject.setFriendlyMessage(friendlyMessage);
			errorObject.setDebugMessage(debugMessage);
			throw new CarbonException(errorObject);
		}
		return targetURIObject;
	}

	private Query composeSPARQLQuery(String queryString) throws CarbonException {
		Query query = null;
		try {
			query = sparqlService.createQuery(queryString, true);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.BAD_REQUEST);
			throw e;
		}
		return query;
	}

	private Object executeSPARQLQuery(Query query, URIObject targetURIObject, Application application) throws CarbonException {
		Object results = null;
		Verb queryVerb = null;
		try {
			queryVerb = sparqlService.getQueryVerb(query);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.BAD_REQUEST);
			throw e;
		}

		switch (queryVerb) {
			case ASK:
				results = executeASKQuery(query, targetURIObject, application);
				break;
			case CONSTRUCT:
				results = executeCONSTRUCTQuery(query, targetURIObject, application);
				break;
			case DESCRIBE:
				results = executeDESCRIBEQuery(query, targetURIObject, application);
				break;
			case SELECT:
				results = executeSELECTQuery(query, targetURIObject, application);
				break;
			default:
				break;
		}

		return results;
	}

	private Boolean executeASKQuery(Query query, URIObject targetURIObject, Application application) throws CarbonException {
		Boolean result = null;
		try {
			result = ldpService.executeASKonLDPRSource(targetURIObject, query, application.getDatasetName());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return result;
	}

	private Model executeCONSTRUCTQuery(Query query, URIObject targetURIObject, Application application) throws CarbonException {
		Model model = null;
		try {
			model = ldpService.executeCONSTRUCTonLDPRSource(targetURIObject, query, application.getDatasetName());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return model;
	}

	private Model executeDESCRIBEQuery(Query query, URIObject targetURIObject, Application application) throws CarbonException {
		Model model = null;
		try {
			model = ldpService.executeDESCRIBEonLDPRSource(targetURIObject, query, application.getDatasetName());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return model;
	}

	private ResultSet executeSELECTQuery(Query query, URIObject targetURIObject, Application application) throws CarbonException {
		ResultSet resultSet = null;
		try {
			resultSet = ldpService.executeSELECTonLDPRSource(targetURIObject, query, application.getDatasetName());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return resultSet;
	}

	private void addHeadersToResponse(HttpServletResponse response, URIObject targetURIObject) {
		addLinkHeader(response);
		addLocationHeader(response, targetURIObject);
	}

	private void addLinkHeader(HttpServletResponse response) {
		HttpHeaderValue linkHeader = new HttpHeaderValue();
		linkHeader.setMainValue(InteractionModel.SPARQL_ENDPOINT.getPrefixedURI().getResourceURI());
		linkHeader.setExtendingKey("rel");
		linkHeader.setExtendingValue("type");

		response.addHeader(HttpHeaders.LINK, linkHeader.toString());
	}

	private void addLocationHeader(HttpServletResponse response, URIObject targetURIObject) {
		response.addHeader(HttpHeaders.LOCATION, targetURIObject.getURI());
	}
}
