package com.base22.carbon.api.ldp.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.api.ldp.handlers.PATCHRequestHandler;

@Controller
public class PATCHController extends AbstractBaseRdfAPIController {

	@Autowired
	private PATCHRequestHandler requestHandler;

	@RequestMapping(value = "/api/ldp/{dataset}/**", method = RequestMethod.PATCH)
	public ResponseEntity<Object> handlePatch(@PathVariable("dataset") String dataset, Model model, HttpServletRequest request, HttpServletResponse response,
			HttpEntity<byte[]> entity) {

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> handlePatch()");
		}

		return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
		// return requestHandler.handlePATCHRequest(dataset, model, request, response, entity);
	}
}
