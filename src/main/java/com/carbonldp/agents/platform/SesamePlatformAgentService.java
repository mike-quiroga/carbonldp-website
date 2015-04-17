package com.carbonldp.agents.platform;

import com.carbonldp.agents.Agent;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.springframework.util.Assert;

public class SesamePlatformAgentService extends AbstractSesameLDPService implements PlatformAgentService {
	private PlatformAgentRepository platformAgentRepository;

	public SesamePlatformAgentService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, PlatformAgentRepository platformAgentRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );

		Assert.notNull( platformAgentRepository );
		this.platformAgentRepository = platformAgentRepository;
	}

	@Override
	public void register( Agent agent ) {
		// TODO: Validate agent
		String email = agent.getEmails().iterator().next();
		if ( platformAgentRepository.existsWithEmail( email ) ) throw new ResourceAlreadyExistsException();
		// TODO: Disable agent
		// TODO: Create agent
		// TODO: Create validation resource
		// TODO: Create "resend validation" resource
		// TODO: Send email with validation code

		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}
}
