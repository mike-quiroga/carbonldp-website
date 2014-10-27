package com.base22.carbon.authentication.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.CarbonException;
import com.base22.carbon.agents.web.AgentsRequestHandler;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Controller
@RequestMapping(value = "/auth")
public class AuthenticationController {

	@Autowired
	private AuthenticationGETRequestHandler loginRequestHandler;
	@Autowired
	private AgentsRequestHandler registerRequestHandler;

	static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(value = "/token", method = RequestMethod.GET)
	public ResponseEntity<Object> getAuthToken(HttpServletRequest request, HttpServletResponse response) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> getAuthToken()");
		}

		try {
			return loginRequestHandler.handleRequest(request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}

	@PreAuthorize("isAnonymous()")
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<Object> register(@RequestBody Model requestModel, HttpServletRequest request, HttpServletResponse response) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> register()");
		}

		try {
			return registerRequestHandler.handleAgentRegistration(requestModel, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}
}