package com.carbonldp.agents;

import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.HTTPUtil;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
	TransactionWrapper transactionWrapper;

	@Transactional
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );
		IRI agentIRI = getAgentIRIString();
		response.addHeader( "Content-Location", agentIRI.stringValue() );

		RDFSource agentSource = sourceService.exists( agentIRI ) ?
			sourceService.get( agentIRI ) :
			transactionWrapper.runInPlatformContext( () -> sourceService.get( agentIRI ) );

		return new ResponseEntity<>( agentSource, HttpStatus.OK );
	}

	private IRI getAgentIRIString() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new AccessDeniedException( "authentication is not an instance of AgentAuthenticationToken" );
		AgentAuthenticationToken agentToken = (AgentAuthenticationToken) authentication;
		return agentToken.getAgent().getSubject();
	}

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) {
		this.transactionWrapper = transactionWrapper;
	}
}
