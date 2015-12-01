package com.carbonldp.apps.roles.web;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.ldp.web.AbstractLDPController;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.web.config.InteractionModel;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.PrimitiveIterator;

@Controller
public class AppRolesController extends AbstractLDPController {

	private AppRolesPUTAgentsHanlder appRolesPUTAgentsHanlder;

	@RequestMapping( method = RequestMethod.POST, value = "apps/*/roles/" )
	public void createAppRole() {
		// TODO: Implement it
		throw new NotImplementedException();
	}

	@RequestMapping( method = RequestMethod.PUT, value = "apps/*/roles/*/agents/" )
	@InteractionModel( APIPreferences.InteractionModel.CONTAINER )
	public ResponseEntity<Object> addAgentToRole( RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return appRolesPUTAgentsHanlder.handleRequest( requestDocument, request, response );
	}

	@Autowired
	public void setAppRolesPUTAgentsHanlder( AppRolesPUTAgentsHanlder appRolesPUTAgentsHanlder ) {this.appRolesPUTAgentsHanlder = appRolesPUTAgentsHanlder;}

}
