package com.carbonldp.authentication.web;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.authentication.Token;
import com.carbonldp.authentication.token.TokenService;
import com.carbonldp.ldp.containers.ResourceMetadata;
import com.carbonldp.ldp.containers.ResourceMetadataDescription;
import com.carbonldp.ldp.containers.ResponseMetadataDescription;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceDescription;
import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import com.carbonldp.rdf.RDFBlankNode;
import com.carbonldp.rdf.RDFResourceDescription;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.HTTPUtil;
import com.carbonldp.utils.ModelUtil;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
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

		addResponseMetadata( token, agentModel );

		return new ResponseEntity<>( token.getBaseModel(), HttpStatus.OK );
	}

	private void addResponseMetadata( Token token, RDFSource agentModel ) {
		RDFBlankNode responseDescription = getResponseMetadata( token );
		int eTag = ModelUtil.calculateETag( agentModel );
		String valueETag = HTTPUtil.formatStrongEtag( eTag );

		BNode bNode = SimpleValueFactory.getInstance().createBNode();

		ResourceMetadata resourceMetadata = new ResourceMetadata( token, bNode );
		resourceMetadata.addType( ResourceMetadataDescription.Resource.CLASS.getIRI() );
		resourceMetadata.addType( RDFResourceDescription.Resource.VOLATILE.getIRI() );
		resourceMetadata.setETag( valueETag );
		resourceMetadata.setResource( agentModel.getIRI() );

		responseDescription.addResponseMetadata( bNode );
	}

	private RDFBlankNode getResponseMetadata( Token token ) {
		ValueFactory valueFactory = SimpleValueFactory.getInstance();
		BNode bNode = valueFactory.createBNode();
		RDFBlankNode responseMetadata = new RDFBlankNode( token, bNode, (Resource) null );
		responseMetadata.add( RDFSourceDescription.Property.TYPE.getIRI(), ResponseMetadataDescription.Resource.CLASS.getIRI() );
		responseMetadata.add( RDFSourceDescription.Property.TYPE.getIRI(), RDFResourceDescription.Resource.VOLATILE.getIRI() );
		return responseMetadata;
	}
}
