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
import com.base22.carbon.security.handlers.ApplicationRoleGETRequestHandler;
import com.base22.carbon.security.handlers.ApplicationRolePUTRequestHandler;
import com.base22.carbon.utils.HttpUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Controller
@RequestMapping(value = "/applications/{appIdentifier}/roles/{appRoleUUID}")
public class ApplicationRoleAPIController {

	@Autowired
	private ApplicationRoleGETRequestHandler getRequestHandler;

	@Autowired
	private ApplicationRolePUTRequestHandler putRequestHandler;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Object> getApplicationRole(@PathVariable("appIdentifier") String appIdentifier, @PathVariable("appRoleUUID") String appRoleUUID,
			HttpServletRequest request, HttpServletResponse response) {

		try {
			return getRequestHandler.handleRequest(appIdentifier, appRoleUUID, request, response);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}
	}

	@RequestMapping(method = RequestMethod.PUT)
	public ResponseEntity<Object> modifyApplicationRole(@PathVariable("appIdentifier") String appIdentifier, @PathVariable("appRoleUUID") String appRoleUUID,
			@RequestBody Model requestModel, HttpServletRequest request, HttpServletResponse response) {

		try {
			return putRequestHandler.replaceApplicationRole(appIdentifier, appRoleUUID, requestModel, request, response);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}
	}

	@RequestMapping(value = "/agents", method = RequestMethod.POST)
	public ResponseEntity<Object> addAgentToRole(@PathVariable("appIdentifier") String appIdentifier, @PathVariable("appRoleUUID") String appRoleUUID,
			@RequestBody Model requestModel, HttpServletRequest request, HttpServletResponse response) {

		return new ResponseEntity<Object>(HttpStatus.NOT_IMPLEMENTED);
	}
}
