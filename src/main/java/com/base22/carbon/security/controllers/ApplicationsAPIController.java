package com.base22.carbon.security.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.security.handlers.ApplicationsGETRequestHandler;
import com.base22.carbon.security.handlers.ApplicationsPOSTRequestHandler;
import com.base22.carbon.utils.HttpUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Controller
@RequestMapping(value = "/applications")
public class ApplicationsAPIController {

	@Autowired
	public ApplicationsGETRequestHandler getRequestHandler;
	@Autowired
	public ApplicationsPOSTRequestHandler postRequestHandler;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Object> getApplications(HttpServletRequest request, HttpServletResponse response) {
		try {
			return getRequestHandler.handleRequest(request, response);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}
	}

	@PreAuthorize("hasAuthority('PRIV_CREATE_APPLICATIONS')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Object> createApplication(@RequestBody Model requestModel, HttpServletRequest request, HttpServletResponse response) {
		try {
			return postRequestHandler.handleRequest(requestModel, request, response);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}
	}
}
