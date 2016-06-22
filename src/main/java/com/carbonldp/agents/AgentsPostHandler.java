package com.carbonldp.agents;

import com.carbonldp.authentication.AnonymousAuthenticationToken;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import org.openrdf.model.IRI;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author NestorVenegas
 * @since 0.14.0-ALPHA
 */
public abstract class AgentsPostHandler extends AbstractRDFPostRequestHandler<Agent> {

	@Override
	protected Agent getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return new Agent( requestBasicContainer );
	}

	@Override
	protected void createChild( IRI targetIRI, Agent documentResourceView ) {
		if ( isAnonymousRequest() ) registerAgent( documentResourceView );
		else createAgent(targetIRI, documentResourceView );
	}

	protected boolean isAnonymousRequest() {
		// TODO: move this function tu SecurityUtil
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication == null || authentication instanceof AnonymousAuthenticationToken;
	}

	protected abstract void createAgent(IRI targetIRI, Agent documentResourceView );

	protected abstract void registerAgent( Agent documentResourceView );

}
