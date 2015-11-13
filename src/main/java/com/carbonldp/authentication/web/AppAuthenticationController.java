package com.carbonldp.authentication.web;

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
 * @since 0.15.0_ALPHA
 */

@Controller
@RequestMapping( "/apps/*/auth-tokens/" )
public class AppAuthenticationController extends AbstractLDPController {

	AuthenticationRequestHandler authenticationHandler;

	@RequestMapping( method = RequestMethod.POST )
	public ResponseEntity<Object> handleRDFPost( HttpServletRequest request, HttpServletResponse response ) {
		return authenticationHandler.handleRequest( request, response );
	}

	@Autowired
	public void setAuthenticationHandler( AuthenticationRequestHandler authenticationHandler ) {
		this.authenticationHandler = authenticationHandler;
	}
}