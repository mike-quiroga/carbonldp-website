package com.carbonldp.agents;

import com.carbonldp.apps.App;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;
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
		AgentAuthenticationToken agentToken = getAgentIRI();
		IRI agentIRI = agentToken.getAgent().getSubject();
		App agentRelatedAppContext = agentToken.getApp();

		RDFSource agentSource = getAgentSource( agentIRI, agentRelatedAppContext );

		response.addHeader( "Content-Location", agentIRI.stringValue() );
		return new ResponseEntity<>( agentSource, HttpStatus.OK );
	}

	private AgentAuthenticationToken getAgentIRI() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new AccessDeniedException( "authentication is not an instance of AgentAuthenticationToken" );
		return (AgentAuthenticationToken) authentication;
	}

	private RDFSource getAgentSource( IRI agentIRI, App agentRelatedAppContext ) {
		App appContext = AppContextHolder.getContext().getApplication();
		if ( appContext == null ) return sourceService.get( agentIRI );
		if ( appContext.equals( agentRelatedAppContext ) ) return sourceService.get( agentIRI );
		return agentRelatedAppContext == null ?
			transactionWrapper.runInPlatformContext( () -> sourceService.get( agentIRI ) ) :
			sourceService.get( agentIRI );
	}

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) {
		this.transactionWrapper = transactionWrapper;
	}
}
