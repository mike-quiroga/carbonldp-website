package com.carbonldp.agents.platform.web;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentService;
import com.carbonldp.agents.AgentsPostHandler;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.ConflictException;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@RequestHandler
public class PlatformAgentsPOSTHandler extends AgentsPostHandler {

	private AgentService platformAgentService;

	protected void createAgent( IRI agentsContainerIRI, Agent documentResourceView ) {
		try {
			platformAgentService.create( agentsContainerIRI, documentResourceView );
		} catch ( ResourceAlreadyExistsException e ) {
			throw new ConflictException( 0x2210 );
		}
	}

	protected void registerAgent( Agent documentResourceView ) {
		try {
			platformAgentService.register( documentResourceView );
		} catch ( ResourceAlreadyExistsException e ) {
			throw new ConflictException( 0x2210 );
		}
	}

	@Autowired
	@Qualifier( "platformAgentService" )
	public void setPlatformAgentService( AgentService platformAgentService ) {
		this.platformAgentService = platformAgentService;
	}
}
