package com.base22.carbon.sparql;

import java.io.ByteArrayOutputStream;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.Application;
import com.base22.carbon.ldp.models.LDPRSource;
import com.base22.carbon.ldp.models.URIObject;
import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;
import com.base22.carbon.sparql.SPARQLService.Verb;
import com.base22.carbon.utils.HTTPUtil;
import com.base22.carbon.web.AbstractRequestHandler;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

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

		Query query = composeSPARQLQuery(queryString);

		LDPRSource targetRDFSource = getTargetRDFSource(targetURIObject, application);

		Object results = executeSPARQLQuery(query, targetRDFSource);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

		ResultSetFormatter.outputAsJSON(byteArrayOutputStream, (ResultSet) results);

		return new ResponseEntity<Object>(byteArrayOutputStream.toString(), HttpStatus.NOT_IMPLEMENTED);
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

	private LDPRSource getTargetRDFSource(URIObject targetURIObject, Application application) throws CarbonException {
		LDPRSource targetRDFSource = null;
		try {
			targetRDFSource = ldpService.getLDPRSourceBranch(targetURIObject, application.getDatasetName());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return targetRDFSource;
	}

	private Object executeSPARQLQuery(Query query, LDPRSource targetRDFSource) throws CarbonException {
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
				break;
			case CONSTRUCT:
				break;
			case DESCRIBE:
				break;
			case SELECT:
				results = executeSELECTQuery(query, targetRDFSource);
				break;
			default:
				break;
		}

		return results;
	}

	private ResultSet executeSELECTQuery(Query query, LDPRSource targetRDFSource) throws CarbonException {
		ResultSet resultSet = null;
		try {
			resultSet = sparqlService.select(query, targetRDFSource.getResource().getModel());
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return resultSet;
	}
}
