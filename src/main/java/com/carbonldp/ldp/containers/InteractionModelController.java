package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences;
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
 * @author MiguelAraCo
 * @since 0.10.0-ALPHA
 */
@Controller( "container:interactionModelController" )
@InteractionModel( APIPreferences.InteractionModel.CONTAINER )
@RequestMapping( "/**" )
public class InteractionModelController {

	private BasePUTRequestHandler putRequestHandler;

	@RequestMapping( method = RequestMethod.PUT )
	public ResponseEntity<Object> handleRDFPUT( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return putRequestHandler.handleRequest( requestDocument, request, response );
	}

	@Autowired
	public void getPutRequestHandler( BasePUTRequestHandler putRequestHandler ) {this.putRequestHandler = putRequestHandler;}
}