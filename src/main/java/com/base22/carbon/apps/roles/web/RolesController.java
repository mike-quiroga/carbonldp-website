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
import com.base22.carbon.apps.roles.web.handlers.RolesGETRequestHandler;
import com.base22.carbon.apps.roles.web.handlers.RolesPOSTRequestHandler;
import com.base22.carbon.utils.HTTPUtil;
import com.hp.hpl.jena.rdf.model.Model;

@Controller
@RequestMapping(value = "/apps/{appIdentifier}/roles")
public class RolesController {

	@Autowired
	private RolesGETRequestHandler getRequestHandler;

	@Autowired
	private RolesPOSTRequestHandler postRequestHandler;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Object> listApplicationRoles(@PathVariable("appIdentifier") String appIdentifier, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			return getRequestHandler.handleRequest(appIdentifier, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}

	@RequestMapping(method = RequestMethod.HEAD)
	public ResponseEntity<Object> headApplicationRoles(@PathVariable("appIdentifier") String appIdentifier, HttpServletRequest request,
			HttpServletResponse response) {

		try {
			return getRequestHandler.handleRequest(appIdentifier, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Object> createApplicationRole(@PathVariable("appIdentifier") String appIdentifier, @RequestBody Model requestModel,
			HttpServletRequest request, HttpServletResponse response) {

		try {
			return postRequestHandler.handleRequest(appIdentifier, requestModel, request, response);
		} catch (CarbonException e) {
			return HTTPUtil.createErrorResponseEntity(e);
		}
	}
}
