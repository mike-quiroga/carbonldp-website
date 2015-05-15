package com.carbonldp.agents.platform.web;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.platform.PlatformAgentService;
import com.carbonldp.authentication.AnonymousAuthenticationToken;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.ConflictException;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RequestHandler
public class PlatformAgentsRDFPostHandler extends AbstractRDFPostRequestHandler<Agent> {

	private PlatformAgentService platformAgentService;

	@Override
	protected void validateDocumentResourceView( Agent documentResourceView ) {
		// TODO: Implement
		// throw new RuntimeException( "Not Implemented" );
	}

	@Override
	protected Agent getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return new Agent( requestBasicContainer );
	}

	@Override
	protected void createChild( URI targetURI, Agent documentResourceView ) {
		if ( isAnonymousRequest() ) registerAgent( documentResourceView );
		else createAgent( documentResourceView );
	}

	private void createAgent( Agent documentResourceView ) {
		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}

	private void registerAgent( Agent documentResourceView ) {
		try {
			platformAgentService.register( documentResourceView );
		} catch ( ResourceAlreadyExistsException e ) {
			throw new ConflictException( "An agent already exists with that email" );
		}
	}

	@Autowired
	public void setPlatformAgentService( PlatformAgentService platformAgentService ) {
		this.platformAgentService = platformAgentService;
	}

	// TODO: Make this accessible in the @RequestMapping annotation
	private boolean isAnonymousRequest() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication == null || authentication instanceof AnonymousAuthenticationToken;
	}
}
