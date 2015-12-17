package com.carbonldp.agents.platform;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.agents.AgentValidator;
import com.carbonldp.agents.SesameAgentsService;
import com.carbonldp.agents.validators.AgentValidatorRepository;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Arrays;

@Transactional
public class SesamePlatformAgentService extends SesameAgentsService {
	private AgentRepository platformAgentRepository;

	public SesamePlatformAgentService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, AgentRepository platformAgentRepository, AgentValidatorRepository agentValidatorRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository, agentValidatorRepository );

		Assert.notNull( platformAgentRepository );
		this.platformAgentRepository = platformAgentRepository;
	}

	@Override
	public void register( Agent agent ) {

		validate( agent );

		String email = agent.getEmails().iterator().next();
		if ( platformAgentRepository.existsWithEmail( email ) ) throw new ResourceAlreadyExistsException();

		boolean requireValidation = configurationRepository.requireAgentEmailValidation();
		if ( requireValidation ) agent.setEnabled( false );
		else agent.setEnabled( true );

		setAgentPasswordFields( agent );

		addAgentToDefaultPlatformRole( agent );

		platformAgentRepository.create( agent );
		ACL agentACL = aclRepository.createACL( agent.getDocument() );
		addAgentDefaultPermissions( agent, agentACL );

		if ( requireValidation ) {
			AgentValidator validator = createAgentValidator( agent );
			ACL validatorACL = aclRepository.createACL( validator.getDocument() );
			addValidatorDefaultPermissions( validatorACL );

			sendValidationEmail( agent, validator );
			// TODO: Create "resend validation" resource
		}
	}

	private void addAgentDefaultPermissions( Agent agent, ACL agentACL ) {
		aclRepository.grantPermissions( agentACL, Arrays.asList( agent ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.UPDATE,
			ACEDescription.Permission.DELETE
		), false );
	}

	private void addAgentToDefaultPlatformRole( Agent agent ) {
		URI defaultPlatformRoleURI = getDefaultPlatformRoleURI();
		URI roleAgentsContainerURI = getRoleAgentsContainerURI( defaultPlatformRoleURI );

		containerRepository.addMember( roleAgentsContainerURI, agent.getURI() );
	}

	private URI getRoleAgentsContainerURI( URI defaultPlatformRoleURI ) {
		// TODO: Use a Vars property
		return new URIImpl( defaultPlatformRoleURI.stringValue() + "agents/" );
	}

	private URI getDefaultPlatformRoleURI() {
		return Platform.Role.APP_DEVELOPER.getURI();
	}

	@Autowired
	public void setConfigurationRepository( ConfigurationRepository configurationRepository ) {
		Assert.notNull( configurationRepository );
		this.configurationRepository = configurationRepository;
	}
}