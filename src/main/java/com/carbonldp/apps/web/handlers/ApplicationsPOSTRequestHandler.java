package com.carbonldp.apps.web.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;

import com.carbonldp.web.AbstractRequestHandler;
import com.carbonldp.web.RequestHandler;

@RequestHandler
public class ApplicationsPOSTRequestHandler extends AbstractRequestHandler {
	public ResponseEntity<Object> handleRequest(HttpServletRequest request, HttpServletResponse response) {

		// TODO: Validate RDFDocument resources
		// -- TODO: Only one document resource
		// -- TODO: No fragment resources
		// TODO: Validate document resource as an Application
		// TODO: Forge the new Application's URI
		// TODO: Change the Application's URI
		// TODO: Create repository for the application
		// TODO: Store repositoryID in the Application
		// TODO: Create default resources in the Application's repository
		// -- TODO: Root Container
		// -- TODO: Application Roles Container
		// -- TODO: ACLs
		// TODO: Create application in the platform's Applications container
		// TODO: Return OK

		return null;
	}
}
