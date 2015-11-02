package com.carbonldp.agents.platform.web;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentsPostHandler;
import com.carbonldp.agents.platform.PlatformAgentService;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.ConflictException;
import org.springframework.beans.factory.annotation.Autowired;

@RequestHandler
public class PlatformAgentsPostHandler extends AgentsPostHandler {

	private PlatformAgentService platformAgentService;

	protected void createAgent( Agent documentResourceView ) {
		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}

	protected void registerAgent( Agent documentResourceView ) {
		try {
			platformAgentService.register( documentResourceView );
		} catch ( ResourceAlreadyExistsException e ) {
			throw new ConflictException( 0x2210 );
		}
	}

	@Autowired
	public void setPlatformAgentService( PlatformAgentService platformAgentService ) {
		this.platformAgentService = platformAgentService;
	}
}
