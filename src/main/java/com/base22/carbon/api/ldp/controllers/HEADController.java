package com.base22.carbon.api.ldp.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.api.ldp.handlers.AbstractRequestHandler;
import com.base22.carbon.api.ldp.handlers.RetrieveRequestHandler;
import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.utils.HttpUtil;

@Controller
public class HEADController extends AbstractRequestHandler {
	@Autowired
	private RetrieveRequestHandler retrieveHandler;

	@RequestMapping(value = "/api/ldp/{application}/**", method = RequestMethod.HEAD)
	public ResponseEntity<Object> handleHEAD(@PathVariable("application") String applicationIdentifier, HttpServletRequest request,
			HttpServletResponse response, HttpEntity<byte[]> entity) {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleHEAD()");
		}

		try {
			return retrieveHandler.handleGET(applicationIdentifier, request, response, entity);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}
	}
}
