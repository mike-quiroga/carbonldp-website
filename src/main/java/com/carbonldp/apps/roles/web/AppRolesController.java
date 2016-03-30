package com.carbonldp.apps.roles.web;

import com.carbonldp.Consts;
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
 * @since 0.18.0-ALPHA
 */

@Controller
@RequestMapping( value = "apps/*/roles/" )
public class AppRolesController extends AbstractLDPController {

	private AppRolesPOSTHandler postHandler;

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
	public ResponseEntity<Object> createAppRole( @RequestBody RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		return postHandler.handleRequest( requestDocument, request, response );
	}

	@Autowired
	public void setAppRolesPOSTHandler( AppRolesPOSTHandler appRolesPostHandler ) {this.postHandler = appRolesPostHandler;}
}
