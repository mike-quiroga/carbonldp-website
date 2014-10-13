package com.base22.carbon.apps.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.web.handlers.AppsGETRequestHandler;
import com.base22.carbon.apps.web.handlers.AppsPOSTRequestHandler;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Controller
@RequestMapping(value = "/apps/")
public class AppsController {

	@Autowired
	public AppsGETRequestHandler getRequestHandler;
	@Autowired
	public AppsPOSTRequestHandler postRequestHandler;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Object> getApplications(HttpServletRequest request, HttpServletResponse response) {
		try {
			return getRequestHandler.handleRequest(request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}

	@RequestMapping(method = RequestMethod.HEAD)
	public ResponseEntity<Object> headApplications(HttpServletRequest request, HttpServletResponse response) {
		try {
			return getRequestHandler.handleRequest(request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}

	@PreAuthorize("hasAuthority('PRIV_CREATE_APPLICATIONS')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Object> createApplication(@RequestBody Model requestModel, HttpServletRequest request, HttpServletResponse response) {
		try {
			return postRequestHandler.handleRequest(requestModel, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}
}
