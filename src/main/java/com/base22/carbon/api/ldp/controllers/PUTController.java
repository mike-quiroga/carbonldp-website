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

import com.base22.carbon.api.ldp.handlers.PUTRequestHandler;

@Controller
public class PUTController extends AbstractBaseRdfAPIController {

	@Autowired
	private PUTRequestHandler requestHandler;

	@RequestMapping(value = { "/apps/{application}/", "/apps/{application}/**" }, method = RequestMethod.PUT)
	public ResponseEntity<Object> handlePut(@PathVariable("application") String applicationIdentifier, HttpServletRequest request,
			HttpServletResponse response, HttpEntity<byte[]> entity) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handlePut()");
		}

		return requestHandler.handlePut(applicationIdentifier, request, response, entity);
	}
}
