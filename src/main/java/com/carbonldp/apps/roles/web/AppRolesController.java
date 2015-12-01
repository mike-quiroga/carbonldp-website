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
@RequestMapping( value = "apps/*/roles/" )
public class AppRolesController extends AbstractLDPController {

	private AppRolesPostHandler postRequestHandler;

	@RequestMapping( method = RequestMethod.POST )
	public ResponseEntity<Object> createAppRole( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return appRolesPostHandler.handleRequest( requestDocument, request, response );
	}

	@InteractionModel( APIPreferences.InteractionModel.CONTAINER )
	@RequestMapping( method = RequestMethod.PUT )
	public void defineParentChildRelation( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {

	}

	@Autowired
	public void setAppRolesPOSTHandler( AppRolesPOSTHandler appRolesPostHandler ) {
		this.appRolesPostHandler = appRolesPostHandler;
	}
}
