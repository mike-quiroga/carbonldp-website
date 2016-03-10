package com.carbonldp.apps.web;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.web.AbstractController;
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
@RequestMapping( value = "/platform/apps/" )
public class AppsController extends AbstractController {
	private AppsRDFPostHandler postRequestHandler;

	@InteractionModel( value = {APIPreferences.InteractionModel.CONTAINER} )
	@RequestMapping( method = RequestMethod.POST )
	public ResponseEntity<Object> createApplication( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return postRequestHandler.handleRequest( requestDocument, request, response );
	}

	@Autowired
	public void setPOSTRequestHandler( AppsRDFPostHandler postRequestHandler ) {
		this.postRequestHandler = postRequestHandler;
	}

}
