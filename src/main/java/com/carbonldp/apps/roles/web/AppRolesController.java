package com.carbonldp.apps.roles.web;

import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AppRolesController {

	private AppRolesDELETEHandler deleteHandler;

	@RequestMapping( method = RequestMethod.POST, value = "apps/*/roles/" )
	public void createAppRole() {
		// TODO: Implement it
		throw new NotImplementedException();
	}

	@RequestMapping( method = RequestMethod.DELETE, value = "apps/*/roles/*/" )
	public ResponseEntity<Object> deleteApp( @RequestBody( required = false ) RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return deleteHandler.handleRequest( requestDocument, request, response );
	}

	@Autowired
	public void setDELETEHandler( AppRolesDELETEHandler deleteHandler ) {
		this.deleteHandler = deleteHandler;
	}
}
