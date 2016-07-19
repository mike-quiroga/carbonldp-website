package com.carbonldp.agents;

import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NestorVenegas
 * @since _version_
 */
@RequestHandler
public class AgentMeHandler extends AbstractLDPRequestHandler {

	@Transactional
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new AccessDeniedException( "authentication is not an instance of AgentAuthenticationToken" );
		AgentAuthenticationToken agentToken = (AgentAuthenticationToken) authentication;
		String agentIRIString = agentToken.getAgent().getSubject().stringValue();
		IRI agentIRI = SimpleValueFactory.getInstance().createIRI( agentIRIString );
		response.setHeader( "Location", agentIRI.stringValue() );
		return new ResponseEntity<>( HttpStatus.SEE_OTHER );
	}
}
