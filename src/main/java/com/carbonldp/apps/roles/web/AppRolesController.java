package com.carbonldp.apps.roles.web;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.ldp.web.AbstractLDPController;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.web.config.InteractionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller

public class AppRolesController extends AbstractLDPController {

	private AppRolesTOCHANGEHandler postHandler;
	private AppRolesPUTHandler putHandler;

	@RequestMapping( method = RequestMethod.POST, value = "apps/*/roles/" )
	public ResponseEntity<Object> createAppRole( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return postHandler.handleRequest( requestDocument, request, response );
	}

	@InteractionModel( APIPreferences.InteractionModel.CONTAINER )
	@RequestMapping( method = RequestMethod.PUT, value = "apps/*/roles/*/" )
	public ResponseEntity<Object> defineParentChildRelation( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return putHandler.handleRequest( requestDocument, request, response );
	}

	@Autowired
	public void setAppRolesPOSTHandler( AppRolesTOCHANGEHandler appRolesPostHandler ) {
		this.postHandler = appRolesPostHandler;
	}

	@Autowired
	public void setAppRolesPutHandler( AppRolesPUTHandler putHandler ) {
		this.putHandler = putHandler;
	}
}
