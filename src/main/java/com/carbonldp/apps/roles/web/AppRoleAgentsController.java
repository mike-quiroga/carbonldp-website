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

/**
 * @author NestorVenegas
 * @since 0.18.0-ALPHA
 */

@Controller
@RequestMapping( value = "apps/*/roles/*/agents/" )
public class AppRoleAgentsController extends AbstractLDPController {

	private AppRoleAgentsPUTHandler appRoleAgentsPUTHandler;
	private AppRoleAgentsDELETEHandler appRoleAgentsDELETEHandler;

	@RequestMapping( method = RequestMethod.PUT )
	@InteractionModel( APIPreferences.InteractionModel.CONTAINER )
	public ResponseEntity<Object> addAgentToRole( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return appRoleAgentsPUTHandler.handleRequest( requestDocument, request, response );
	}

	@RequestMapping( method = RequestMethod.DELETE )
	@InteractionModel( APIPreferences.InteractionModel.CONTAINER )
	public ResponseEntity<Object> removeAgentFromRole( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return appRoleAgentsDELETEHandler.handleRequest( requestDocument, request, response );
	}


	@Autowired
	public void setAppRoleAgentsPUTHandler( AppRoleAgentsPUTHandler appRoleAgentsPUTHandler ) { this.appRoleAgentsPUTHandler = appRoleAgentsPUTHandler; }

	@Autowired
	public void setAppRoleAgentsDELETEHandler( AppRoleAgentsDELETEHandler appRoleAgentsDELETEHandler ) { this.appRoleAgentsDELETEHandler = appRoleAgentsDELETEHandler; }
}
