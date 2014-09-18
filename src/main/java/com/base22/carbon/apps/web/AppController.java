package com.base22.carbon.apps.web;

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
import com.base22.carbon.apps.web.handlers.AppGETRequestHandler;
import com.base22.carbon.apps.web.handlers.AppPUTRequestHandler;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Controller
@RequestMapping(value = "/apps/{applicationIdentifer}")
public class AppController {

	@Autowired
	protected AppGETRequestHandler getRequestHandler;
	@Autowired
	protected AppPUTRequestHandler putRequestHandler;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Object> getApplication(@PathVariable("applicationIdentifer") String applicationIdentifier, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			return getRequestHandler.handleRequest(applicationIdentifier, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}

	@RequestMapping(method = RequestMethod.HEAD)
	public ResponseEntity<Object> headApplication(@PathVariable("applicationIdentifer") String applicationIdentifier, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			return getRequestHandler.handleRequest(applicationIdentifier, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Object> modifyApplication(@PathVariable("applicationIdentifer") String applicationIdentifier, @RequestBody Model requestModel,
			HttpServletRequest request, HttpServletResponse response) {
		try {
			return putRequestHandler.handleRequest(applicationIdentifier, requestModel, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}

	@RequestMapping(method = RequestMethod.DELETE)
	public ResponseEntity<String> deleteApplication(@PathVariable("applicationIdentifer") String applicationIdentifier, HttpServletRequest request,
			HttpServletResponse response) {
		return new ResponseEntity<String>(HttpStatus.NOT_IMPLEMENTED);
	}
}
