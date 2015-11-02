package com.carbonldp.agents;

import com.carbonldp.authentication.AnonymousAuthenticationToken;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import org.openrdf.model.URI;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author NestorVenegas
 * @since _version_
 */
public abstract class AgentsPostHandler extends AbstractRDFPostRequestHandler<Agent> {

	@Override
	protected Agent getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return new Agent( requestBasicContainer );
	}

	@Override
	protected void createChild( URI targetURI, Agent documentResourceView ) {
		if ( isAnonymousRequest() ) registerAgent( documentResourceView );
		else createAgent( documentResourceView );
	}

	protected boolean isAnonymousRequest() {
		// TODO: move this function tu SecurityUtil
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication == null || authentication instanceof AnonymousAuthenticationToken;
	}

	protected abstract void createAgent( Agent documentResourceView );

	protected abstract void registerAgent( Agent documentResourceView );

}
