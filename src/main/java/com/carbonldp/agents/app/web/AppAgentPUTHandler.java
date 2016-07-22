package com.carbonldp.agents.app.web;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentService;
import com.carbonldp.ldp.sources.AbstractPUTRequestHandler;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author NestorVenegas
 * @since 0.39.0
 */

@RequestHandler
public class AppAgentPUTHandler extends AbstractPUTRequestHandler<Agent> {
	private AgentService appAgentService;

	@Override
	protected Agent getDocumentResourceView( RDFResource requestDocumentResource ) {
		return new Agent( requestDocumentResource );
	}

	@Override
	protected void replaceResource( IRI targetIRI, Agent documentResourceView ) {
		appAgentService.replace( targetIRI, documentResourceView );
	}

	@Autowired
	public void setAppAgentService( AgentService appAgentService ) {
		this.appAgentService = appAgentService;
	}
}
