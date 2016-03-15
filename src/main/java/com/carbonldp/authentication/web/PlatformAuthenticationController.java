package com.carbonldp.authentication.web;

import com.carbonldp.Consts;
import com.carbonldp.ldp.web.AbstractLDPController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NestorVenegas
 * @since 0.15.0-ALPHA
 */

@Controller
@RequestMapping( "/platform/auth-tokens/" )
public class PlatformAuthenticationController extends AbstractLDPController {

	AuthenticationRequestHandler authenticationHandler;

	@RequestMapping( method = RequestMethod.POST, consumes = {
		Consts.TURTLE,
		Consts.JSONLD,
		Consts.RDFJSON,
		Consts.RDFXML,
		Consts.TRIG
	} )
	public ResponseEntity<Object> handleRDFPost( HttpServletRequest request, HttpServletResponse response ) {
		return authenticationHandler.handleRequest( request, response );
	}

	@Autowired
	public void setAuthenticationHandler( AuthenticationRequestHandler authenticationHandler ) {
		this.authenticationHandler = authenticationHandler;
	}
}
