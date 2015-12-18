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
public class AppRoleController extends AbstractLDPController {

	private AppRolePUTHandler putHandler;
	private AppRolePOSTHandler postHandler;
	private AppRolesDELETEHandler deleteHandler;

	@RequestMapping( method = RequestMethod.PUT, value = "apps/*/roles/*/" )
	@InteractionModel( APIPreferences.InteractionModel.CONTAINER )
	public ResponseEntity<Object> defineParentChildRelation( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return putHandler.handleRequest( requestDocument, request, response );
	}

	@RequestMapping( method = RequestMethod.DELETE )
	public ResponseEntity<Object> deleteApp( @RequestBody( required = false ) RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return deleteHandler.handleRequest( requestDocument, request, response );
	}

	@Autowired
	public void setDELETEHandler( AppRolesDELETEHandler deleteHandler ) {
		this.deleteHandler = deleteHandler;
	}

	@RequestMapping( method = RequestMethod.POST, value = "apps/*/roles/" )
	public ResponseEntity<Object> createAppRole( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return postHandler.handleRequest( requestDocument, request, response );
	}

	@Autowired
	public void setAppRolesPutHandler( AppRolePUTHandler putHandler ) {
		this.putHandler = putHandler;
	}

	@Autowired
	public void setAppRolesPOSTHandler( AppRolePOSTHandler appRolePostHandler ) {
		this.postHandler = appRolePostHandler;
	}
}
