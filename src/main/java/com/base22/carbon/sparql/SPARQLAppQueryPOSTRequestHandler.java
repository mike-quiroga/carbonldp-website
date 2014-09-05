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
import com.base22.carbon.HttpHeaders;
import com.base22.carbon.apps.Application;
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
public class SPARQLAppQueryPOSTRequestHandler extends AbstractRequestHandler {
	public ResponseEntity<Object> handleRequest(String appIdentifier, String queryString, HttpServletRequest request, HttpServletResponse response)
			throws CarbonException {

		Application targetApplication = getTargetApplication(appIdentifier);
		if ( ! targetApplicationExists(targetApplication) ) {
			return handleNonExistentApplication(appIdentifier, request, response);
		}

		queryString = SPARQLUtil.setDefaultNSPrefixes(queryString, true);

		Query query = composeSPARQLQuery(queryString);

		Object result = executeSPARQLQuery(query, targetApplication);

		addHeadersToResponse(response, targetApplication);

		return new ResponseEntity<Object>(result, HttpStatus.OK);
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

	private Object executeSPARQLQuery(Query query, Application application) throws CarbonException {
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
				results = executeASKQuery(query, application);
				break;
			case CONSTRUCT:
				results = executeCONSTRUCTQuery(query, application);
				break;
			case DESCRIBE:
				results = executeDESCRIBEQuery(query, application);
				break;
			case SELECT:
				results = executeSELECTQuery(query, application);
				break;
			default:
				break;
		}

		return results;
	}

	private Boolean executeASKQuery(Query query, Application application) throws CarbonException {
		Boolean result = null;
		try {
			result = sparqlService.ask(query, application);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return result;
	}

	private Model executeCONSTRUCTQuery(Query query, Application application) throws CarbonException {
		Model model = null;
		try {
			model = sparqlService.construct(query, application);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return model;
	}

	private Model executeDESCRIBEQuery(Query query, Application application) throws CarbonException {
		Model model = null;
		try {
			model = sparqlService.describe(query, application);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return model;
	}

	private ResultSet executeSELECTQuery(Query query, Application application) throws CarbonException {
		ResultSet resultSet = null;
		try {
			resultSet = sparqlService.select(query, application);
		} catch (CarbonException e) {
			e.getErrorObject().setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			throw e;
		}
		return resultSet;
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

		response.addHeader(HttpHeaders.LINK, linkHeader.toString());
	}
}
