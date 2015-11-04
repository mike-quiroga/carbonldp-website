package com.carbonldp.authentication.web;

import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authentication.TokenRepresentation;
import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class AuthenticationRequestHandler extends AbstractLDPRequestHandler {

	@Autowired
	TokenService tokenService;

	@Transactional
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new RuntimeException( "authentication is not an instance of AgentAuthenticationToken" );
		AgentAuthenticationToken agentToken = (AgentAuthenticationToken) authentication;
		Token token = tokenService.allocateToken( agentToken.getAgent().getSubject().stringValue() );

		TokenRepresentation tokenRepresentation = getTokenRepresentation( token );

	}

	private TokenRepresentation getTokenRepresentation( Token token ) {
		TokenRepresentation tokenRepresentation = new TokenRepresentation();

	}

}
