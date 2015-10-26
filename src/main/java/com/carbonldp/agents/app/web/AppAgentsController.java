package com.carbonldp.agents.app.web;

import com.carbonldp.ldp.web.AbstractLDPController;
import com.carbonldp.rdf.RDFDocument;
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
 * @since _version_
 */

@Controller
@RequestMapping( value = "/apps/*/agents/" )
public class AppAgentsController extends AbstractLDPController {

	private AppAgentsRDFPostHandler postRequestHandler;
	private AppAgentsRDFDeleteHandler deleteRequestHandler;

	@RequestMapping( method = RequestMethod.POST, consumes = {
		"application/ld+json",
		"text/turtle"
	} )
	public ResponseEntity<Object> registerAgent( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return postRequestHandler.handleRequest( requestDocument, request, response );
	}

	@RequestMapping( method = RequestMethod.DELETE )
	public ResponseEntity<Object> deleteAgent( HttpServletRequest request, HttpServletResponse response ) {
		return deleteRequestHandler.handleRequest( request, response );
	}

	@Autowired
	public void setPOSTRequestHandler( AppAgentsRDFPostHandler postRequestHandler ) {
		this.postRequestHandler = postRequestHandler;
	}

	@Autowired
	public void setDeleteRequestHandler( AppAgentsRDFDeleteHandler deleteRequestHandler ) {
		this.deleteRequestHandler = deleteRequestHandler;
	}

}
