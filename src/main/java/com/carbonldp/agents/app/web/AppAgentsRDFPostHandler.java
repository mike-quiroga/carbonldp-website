package com.carbonldp.agents.app.web;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.app.AppAgentService;
import com.carbonldp.apps.App;
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

/**
 * @author NestorVenegas
 * @since _version_
 */

@RequestHandler
public class AppAgentsRDFPostHandler extends AbstractRDFPostRequestHandler<Agent> {

	private AppAgentService appAgentService;

	@Override
	protected Agent getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return new Agent( requestBasicContainer );
	}

	@Override
	protected void createChild( URI targetURI, Agent documentResourceView ) {
		App app = new App( sourceService.get( targetURI ) );
		if ( isAnonymousRequest() ) registerAgent( app, documentResourceView );
		else createAgent( app, documentResourceView );
	}

	private void createAgent( App app, Agent documentResourceView ) {
		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}

	private void registerAgent( App app, Agent documentResourceView ) {
		try {
			appAgentService.register( app, documentResourceView );
		} catch ( ResourceAlreadyExistsException e ) {
			throw new ConflictException( 0x2210 );
		}
	}

	private boolean isAnonymousRequest() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication == null || authentication instanceof AnonymousAuthenticationToken;
	}

	@Autowired
	public void setAppAgentService( AppAgentService appAgentService ) {
		this.appAgentService = appAgentService;
	}

}
