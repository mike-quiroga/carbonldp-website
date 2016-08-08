package com.carbonldp.agents.platform.web;

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

@Controller
@RequestMapping( value = "/platform/agents/*/" )
public class PlatformAgentController extends AbstractLDPController {

	private PlatformAgentsDELETEHandler deleteRequestHandler;

	@RequestMapping( method = RequestMethod.DELETE )
	public ResponseEntity<Object> deleteAgent( @RequestBody( required = false ) RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return deleteRequestHandler.handleRequest( requestDocument, request, response );
	}

	@Autowired
	public void setDeleteRequestHandler( PlatformAgentsDELETEHandler deleteRequestHandler ) {
		this.deleteRequestHandler = deleteRequestHandler;
	}
}
