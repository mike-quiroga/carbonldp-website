package com.base22.carbon.api.ldp.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.api.ldp.handlers.DELETERequestHandler;
import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.utils.HttpUtil;

@Controller
public class DELETEController extends AbstractBaseRdfAPIController {

	@Autowired
	private DELETERequestHandler requestHandler;

	@RequestMapping(value = "/ldp/{application}/**", method = RequestMethod.DELETE)
	public ResponseEntity<Object> handleDelete(@PathVariable("application") String applicationIdentifier, HttpServletRequest request,
			HttpServletResponse response) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handleDelete()");
		}

		try {
			return requestHandler.handleDelete(applicationIdentifier, request, response);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}
	}
}
