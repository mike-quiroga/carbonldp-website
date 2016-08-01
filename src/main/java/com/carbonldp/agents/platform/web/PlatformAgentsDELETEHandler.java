package com.carbonldp.agents.platform.web;

import com.carbonldp.agents.AgentService;
import com.carbonldp.ldp.web.AbstractDELETERequestHandler;
import com.carbonldp.web.RequestHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.eclipse.rdf4j.model.IRI;

/**
 * @author NestorVenegas
 * @since 0.14.0-ALPHA
 */

@RequestHandler
public class PlatformAgentsDELETEHandler extends AbstractDELETERequestHandler {

	private AgentService platformAgentService;

	@Override
	public void delete( IRI targetIRI ) {
		platformAgentService.delete( targetIRI );
	}

	@Autowired
	@Qualifier( "platformAgentService" )
	public void setPlatformAgentService( AgentService platformAgentService ) {
		this.platformAgentService = platformAgentService;
	}
}
