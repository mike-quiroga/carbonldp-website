package com.carbonldp.authentication.ldapServer.web;

import com.carbonldp.Consts;
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

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since 0.37.0
 */

@Controller
@RequestMapping( value = "/platform/apps/*/ldap-servers/" )
public class LDAPServersController extends AbstractController {
	private LDAPServerPOSTHandler postRequestHandler;

	@InteractionModel( value = {APIPreferences.InteractionModel.CONTAINER}, handlesDefault = true )
	@RequestMapping( method = RequestMethod.POST, consumes = {
		Consts.RDFMediaTypes.TURTLE,
		Consts.RDFMediaTypes.JSON_LD,
		Consts.RDFMediaTypes.JSON_RDF,
		Consts.RDFMediaTypes.XML_RDF,
		Consts.RDFMediaTypes.TRIG,
		Consts.RDFMediaTypes.N_TRIPLES,
		Consts.RDFMediaTypes.N3,
		Consts.RDFMediaTypes.TRIX,
		Consts.RDFMediaTypes.BINARY,
		Consts.RDFMediaTypes.N_QUADS
	} )
	public ResponseEntity<Object> createLDAPServer( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return postRequestHandler.handleRequest( requestDocument, request, response );
	}

	@Autowired
	public void setPostRequestHandler( LDAPServerPOSTHandler postRequestHandler ) {
		this.postRequestHandler = postRequestHandler;
	}
}
