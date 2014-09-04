package com.base22.carbon.sparql;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.CarbonException;
import com.base22.carbon.utils.HTTPUtil;
import com.base22.carbon.web.AbstractController;

@Controller
public class SPARQLController extends AbstractController {
	@Autowired
	protected SPARQLQueryPOSTRequestHandler queryHandler;

	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.POST, consumes = "application/sparql-query")
	public ResponseEntity<Object> handleSPARQLQuery(@PathVariable("application") String applicationIdentifier, @RequestBody String query,
			HttpServletRequest request, HttpServletResponse response) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleSPARQLQuery()");
		}

		try {
			return queryHandler.handleRequest(applicationIdentifier, query, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}

	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.POST, consumes = "application/sparql-update")
	public ResponseEntity<Object> handleSPARQLUpdate(@PathVariable("application") String applicationIdentifier, @RequestBody String query,
			HttpServletRequest request, HttpServletResponse response) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleSPARQLUpdate()");
		}

		return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
	}

	@RequestMapping(value = { "/apps/{application}" }, method = RequestMethod.POST, consumes = "application/sparql-query")
	public ResponseEntity<Object> handleAppSPARQLQuery(@PathVariable("application") String applicationIdentifier, @RequestBody String query,
			HttpServletRequest request, HttpServletResponse response) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleAppSPARQLQuery()");
		}

		return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
	}

	@RequestMapping(value = { "/apps/{application}" }, method = RequestMethod.POST, consumes = "application/sparql-update")
	public ResponseEntity<Object> handleAppSPARQLUpdate(@PathVariable("application") String applicationIdentifier, @RequestBody String query,
			HttpServletRequest request, HttpServletResponse response) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleAppSPARQLUpdate()");
		}

		return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
	}

}
