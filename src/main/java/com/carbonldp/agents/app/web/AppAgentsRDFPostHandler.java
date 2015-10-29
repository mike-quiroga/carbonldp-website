package com.carbonldp.agents.app.web;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentsRDFPostHandler;
import com.carbonldp.agents.app.AppAgentService;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.ConflictException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author NestorVenegas
 * @since _version_
 */

@RequestHandler
public class AppAgentsRDFPostHandler extends AgentsRDFPostHandler {

	private AppAgentService appAgentService;

	protected void createAgent( Agent documentResourceView ) {
		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}

	protected void registerAgent( Agent documentResourceView ) {
		try {
			appAgentService.register( documentResourceView );
		} catch ( ResourceAlreadyExistsException e ) {
			throw new ConflictException( 0x2210 );
		}
	}

	@Autowired
	public void setAppAgentService( AppAgentService appAgentService ) {
		this.appAgentService = appAgentService;
	}

}
