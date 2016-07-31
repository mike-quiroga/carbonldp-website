package com.carbonldp.agents.app;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentValidator;
import com.carbonldp.agents.SesameAgentsService;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.ldp.containers.ContainerService;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author NestorVenegas
 * @since 0.14.0-ALPHA
 */

public class SesameAppAgentService extends SesameAgentsService {

	protected AppRoleRepository appRoleRepository;

	@Override
	public Agent get( IRI agentIRI ) {
		return agentRepository.get( agentIRI );
	}

	public void delete( IRI agentIRI ) {
		sourceRepository.delete( agentIRI, true );
	}

	@Autowired
	public void setAppAgentRepository( AppAgentRepository appAgentRepository ) { this.agentRepository = appAgentRepository; }

	@Autowired
	public void setAppRoleRepository( AppRoleRepository appRoleRepository ) { this.appRoleRepository = appRoleRepository; }
}
