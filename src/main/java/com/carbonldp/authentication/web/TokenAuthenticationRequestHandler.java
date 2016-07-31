package com.carbonldp.authentication.web;

import com.carbonldp.agents.AgentService;
import com.carbonldp.authentication.Token;
import com.carbonldp.authentication.token.TokenService;
import com.carbonldp.ldp.containers.*;
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

	AgentService appAgentService;

	AgentService platformAgentService;

	@Transactional
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		Token token = tokenService.createToken();

		IRI agentIRI = token.getCredentialsOf();
		RDFSource agentModel;
		agentModel = token.getRelatedApp() == null ?
			platformAgentService.get( agentIRI ) :
			appAgentService.get( agentIRI );

		token.getBaseModel().addAll( agentModel );

		addResponseMetadata( token, agentModel );

		return new ResponseEntity<>( token.getBaseModel(), HttpStatus.OK );
	}

	private void addResponseMetadata( Token token, RDFSource agentModel ) {
		RDFBlankNode responseDescription = ResponseMetadataFactory.getInstance().getResponseMetadata( token );
		ResourceMetadataFactory.getInstance().create( token.getBaseModel(),responseDescription, agentModel);
	}

	@Autowired
	@Qualifier( "appAgentService" )
	public void setAppAgentService( AgentService appAgentService ) {this.appAgentService = appAgentService;}

	@Autowired
	@Qualifier( "platformAgentService" )
	public void setPlatformAgentService( AgentService platformAgentService ) {this.platformAgentService = platformAgentService;}
}
