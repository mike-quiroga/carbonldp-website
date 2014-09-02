package com.base22.carbon.apps.roles.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.base22.carbon.CarbonException;
import com.base22.carbon.HttpUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Controller
@RequestMapping(value = "/apps/{appIdentifier}/roles/{appRoleUUID}")
public class ApplicationRoleAPIController {

	@Autowired
	private ApplicationRoleGETRequestHandler getRequestHandler;

	@Autowired
	private ApplicationRolePUTRequestHandler putRequestHandler;

	@Autowired
	private ApplicationRolePOSTRequestHandler postRequestHandler;

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

		try {
			return postRequestHandler.handleRequest(appIdentifier, appRoleUUID, requestModel, request, response);
		} catch (CarbonException e) {
			return HttpUtil.createErrorResponseEntity(e);
		}
	}
}
