package com.carbonldp.agents.app.web;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentService;
import com.carbonldp.agents.AgentsPostHandler;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.ConflictException;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author NestorVenegas
 * @since 0.14.0-ALPHA
 */

@RequestHandler
public class AppAgentsPOSTHandler extends AgentsPostHandler {

	private AgentService appAgentService;

	protected void createAgent( IRI agentContainerIRI, Agent documentResourceView ) {
		try {
			appAgentService.create( agentContainerIRI, documentResourceView );
		} catch ( ResourceAlreadyExistsException e ) {
			throw new ConflictException( 0x2210 );
		}

	}

	protected void registerAgent( Agent documentResourceView ) {
		try {
			appAgentService.register( documentResourceView );
		} catch ( ResourceAlreadyExistsException e ) {
			throw new ConflictException( 0x2210 );
		}
	}

	@Autowired
	@Qualifier( "appAgentService" )
	public void setAppAgentService( AgentService appAgentService ) {
		this.appAgentService = appAgentService;
	}

}
