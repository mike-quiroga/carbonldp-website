package com.carbonldp.authentication.web;

import com.carbonldp.ldp.web.AbstractLDPController;
import com.carbonldp.rdf.RDFDocument;
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
@RequestMapping( "/auth-tokens" )
public class PlatformAuthenticationController extends AbstractLDPController {

	AuthenticationRequestHandler authenticationHandler;

	@RequestMapping( method = RequestMethod.POST )
	public ResponseEntity<Object> handleRDFPost( HttpServletRequest request, HttpServletResponse response ) {
		return authenticationHandler.handleRequest( request, response );
	}
}
