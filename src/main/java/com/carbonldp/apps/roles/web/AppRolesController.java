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

	private AppRolesDELETEAgentsHandler appRolesDELETEAgentsHandler;
	private AppRolesPOSTHandler postHandler;

	@RequestMapping( method = RequestMethod.POST )
	public ResponseEntity<Object> createAppRole( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return postHandler.handleRequest( requestDocument, request, response );
	}

	@RequestMapping( method = RequestMethod.DELETE, value = "apps/*/roles/*/agents/" )
	@InteractionModel( APIPreferences.InteractionModel.CONTAINER )
	public ResponseEntity<Object> deleteApp( @RequestBody( required = true ) RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return appRolesDELETEAgentsHandler.handleRequest( requestDocument, request, response );
	}

	@Autowired
	public void setAppRolesDELETEAgentsHandler( AppRolesDELETEAgentsHandler appRolesDELETEAgentsHandler ) {this.appRolesDELETEAgentsHandler = appRolesDELETEAgentsHandler;}

	@Autowired
	public void setAppRolesPOSTHandler( AppRolesPOSTHandler appRolesPostHandler ) {this.postHandler = appRolesPostHandler;}
}
