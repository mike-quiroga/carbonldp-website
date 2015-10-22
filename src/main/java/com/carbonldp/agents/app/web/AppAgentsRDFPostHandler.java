package com.carbonldp.agents.app.web;

import com.carbonldp.agents.Agent;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import org.openrdf.model.URI;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class AppAgentsRDFPostHandler extends AbstractRDFPostRequestHandler<Agent> {

	@Override
	protected Agent getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return new Agent( requestBasicContainer );
	}

	@Override
	protected void createChild( URI targetURI, Agent documentResourceView ) {
		if ( isAnonymousRequest() ) registerAgent( documentResourceView );
		else createAgent( documentResourceView );
	}
}
