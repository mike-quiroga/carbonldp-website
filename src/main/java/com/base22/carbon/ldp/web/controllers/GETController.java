package com.base22.carbon.ldp.web.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ldp.web.handlers.GETRequestHandler;
import com.base22.carbon.utils.HTTPUtil;

@Controller
public class GETController extends AbstractLDPController {

	@Autowired
	private GETRequestHandler retrieveHandler;

	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.GET)
	public ResponseEntity<Object> handleGET(@PathVariable("application") String applicationIdentifier, HttpServletRequest request, HttpServletResponse response) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleGET()");
		}

		try {
			return retrieveHandler.handleRetrieve(applicationIdentifier, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}
}
