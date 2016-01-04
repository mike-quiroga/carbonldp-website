package com.carbonldp.agents.platform;

import com.carbonldp.agents.SesameAgentsRepository;
import com.carbonldp.apps.context.RunInPlatformContext;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SesamePlatformAgentRepository extends SesameAgentsRepository implements PlatformAgentRepository {
	private final URI agentsContainerURI;

	public SesamePlatformAgentRepository( SesameConnectionFactory connectionFactory, RDFSourceRepository sourceRepository, ContainerRepository containerRepository,
		URI agentsContainerURI ) {
		super( connectionFactory, sourceRepository, containerRepository );
		this.agentsContainerURI = agentsContainerURI;
	}

	@Override
	protected URI getAgentsContainerURI() {
		return agentsContainerURI;
	}

	@RunInPlatformContext
	@Override
	public boolean exists( URI agentURI ) {
		return containerRepository.hasMember( getAgentsContainerURI(), agentURI, agentsContainerType );
	}
}