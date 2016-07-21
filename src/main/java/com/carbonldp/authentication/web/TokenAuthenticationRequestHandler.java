package com.carbonldp.authentication.web;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.authentication.Token;
import com.carbonldp.authentication.token.TokenService;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NestorVenegas
 * @since 0.15.0-ALPHA
 */
@RequestHandler
public class TokenAuthenticationRequestHandler extends AbstractLDPRequestHandler {

	@Autowired
	TokenService tokenService;

	@Autowired
	TransactionWrapper transactionWrapper;

	@Autowired
	@Qualifier( "platformAgentRepository" )
	AgentRepository platformAgentRepository;
	@Autowired
	@Qualifier( "appAgentRepository" )
	AgentRepository appAgentRepository;

	@Transactional
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		Token token = tokenService.createToken();

		IRI agentIRI = token.getCredentialsOf();
		RDFSource agentModel;
		if ( platformAgentRepository.exists( agentIRI ) ) {
			agentModel = platformAgentRepository.get( agentIRI );
		} else {
			agentModel = appAgentRepository.get( agentIRI );
		}
		token.getBaseModel().addAll( agentModel );

		return new ResponseEntity<>( token.getBaseModel(), HttpStatus.OK );

	}
}
