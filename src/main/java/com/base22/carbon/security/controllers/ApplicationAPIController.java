package com.base22.carbon.security.controllers;

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

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.security.handlers.ApplicationGETRequestHandler;
import com.base22.carbon.security.handlers.ApplicationPUTRequestHandler;
import com.base22.carbon.utils.HttpUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Controller
@RequestMapping(value = "/api/applications/{applicationIdentifer}")
public class ApplicationAPIController {

	@Autowired
	protected ApplicationGETRequestHandler getRequestHandler;
	@Autowired
	protected ApplicationPUTRequestHandler putRequestHandler;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Object> getApplication(@PathVariable("applicationIdentifer") String applicationIdentifier, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			return getRequestHandler.handleRequest(applicationIdentifier, request, response);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Object> modifyApplication(@PathVariable("applicationIdentifer") String applicationIdentifier, @RequestBody Model requestModel,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			return putRequestHandler.handleRequest(applicationIdentifier, requestModel, request, response);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}
	}

	@RequestMapping(value = "/api/applications/{application}", method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteApplication(@PathVariable("applicationIdentifer") String applicationIdentifier, HttpServletRequest request,
			HttpServletResponse response) {
		return new ResponseEntity<String>(HttpStatus.NOT_IMPLEMENTED);
	}
}
