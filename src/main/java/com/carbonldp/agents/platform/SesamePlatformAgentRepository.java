package com.carbonldp.agents.platform;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.SesameAgentsRepository;
import com.carbonldp.apps.context.RunInPlatformContext;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SesamePlatformAgentRepository extends SesameAgentsRepository implements PlatformAgentRepository {
	private final IRI agentsContainerIRI;

	public SesamePlatformAgentRepository( SesameConnectionFactory connectionFactory, RDFSourceRepository sourceRepository, ContainerRepository containerRepository,
		IRI agentsContainerIRI ) {
		super( connectionFactory, sourceRepository, containerRepository );
		this.agentsContainerIRI = agentsContainerIRI;
	}

	@Override
	public IRI getAgentsContainerIRI() {
		return agentsContainerIRI;
	}

	@RunInPlatformContext
	@Override
	public Agent get( IRI iri ) {
		return super.get( iri );
	}

	@RunInPlatformContext
	@Override
	public boolean exists( IRI agentIRI ) {
		return containerRepository.hasMember( getAgentsContainerIRI(), agentIRI, agentsContainerType );
	}
}
