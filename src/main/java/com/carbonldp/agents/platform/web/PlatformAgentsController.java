package com.carbonldp.agents.platform.web;

import com.carbonldp.ldp.web.AbstractLDPController;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.web.exceptions.NotImplementedException;
import com.github.jsonldjava.utils.Obj;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping( value = "/platform/agents/" )
public class PlatformAgentsController extends AbstractLDPController {

	private PlatformAgentsRDFPostHandler postRequestHandler;
	private PlatformAgentsRDFDeleteHandler deleteRequestHandler;

	@RequestMapping( method = RequestMethod.POST, consumes = {
		"application/ld+json",
		"text/turtle"
	} )
	public ResponseEntity<Object> registerAgent( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return postRequestHandler.handleRequest( requestDocument, request, response );
	}

	@RequestMapping( method = RequestMethod.DELETE )
	public ResponseEntity<Object> deleteAgent(HttpServletRequest request, HttpServletResponse response) {
		return deleteRequestHandler.handleRequest(request,response);
	}

	@Autowired
	public void setPOSTRequestHandler( PlatformAgentsRDFPostHandler postRequestHandler ) {
		this.postRequestHandler = postRequestHandler;
	}
}
