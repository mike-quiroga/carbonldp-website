package com.carbonldp.authentication.web;

import com.carbonldp.authentication.Token;
import com.carbonldp.authentication.token.AuthenticationService;
import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NestorVenegas
 * @since _version_
 */
@RequestHandler
public class AuthenticationRequestHandler extends AbstractLDPRequestHandler {

	@Autowired
	AuthenticationService authenticationService;

	@Transactional
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		Token token = authenticationService.createToken();

		return new ResponseEntity<>( token, HttpStatus.OK );

	}
}
