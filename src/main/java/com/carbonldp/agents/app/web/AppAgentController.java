package com.carbonldp.agents.app.web;

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
 * @since _version_
 */

@Controller
@RequestMapping( value = "/apps/*/agents/*/" )
public class AppAgentController extends AbstractLDPController {
	private AppAgentPUTHandler putHandler;

	@RequestMapping( method = RequestMethod.PUT )
	@InteractionModel( APIPreferences.InteractionModel.RDF_SOURCE )
	public ResponseEntity<Object> editAgent( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return putHandler.handleRequest( requestDocument, request, response );
	}

	@Autowired
	public void setPutHandler( AppAgentPUTHandler putHandler ) {
		this.putHandler = putHandler;
	}
}

