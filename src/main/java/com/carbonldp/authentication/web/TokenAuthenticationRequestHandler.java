package com.carbonldp.authentication.web;


import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authentication.Token;
import com.carbonldp.authentication.token.TokenService;
import com.carbonldp.ldp.containers.ResourceMetadataFactory;
import com.carbonldp.ldp.containers.ResponseMetadataFactory;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import com.carbonldp.rdf.RDFBlankNode;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Transactional
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		Token token = tokenService.createToken();

		IRI agentIRI = token.getCredentialsOf();
		RDFSource agentModel;

		agentModel = ( ( token.getRelatedApp() == null ) && ( AppContextHolder.getContext().getApplication() != null ) ) ?
			transactionWrapper.runInPlatformContext( () -> sourceService.get( agentIRI ) ) :
			sourceService.get( agentIRI );

		token.getBaseModel().addAll( agentModel );

		addResponseMetadata( token, agentModel );

		return new ResponseEntity<>( token.getBaseModel(), HttpStatus.OK );
	}

	private void addResponseMetadata( Token token, RDFSource agentModel ) {
		RDFBlankNode responseDescription = ResponseMetadataFactory.getInstance().getResponseMetadata( token );
		ResourceMetadataFactory.getInstance().create( token.getBaseModel(), responseDescription, agentModel );
	}
}
