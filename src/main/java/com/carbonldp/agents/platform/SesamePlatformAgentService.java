package com.carbonldp.agents.platform;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentValidator;
import com.carbonldp.agents.SesameAgentsService;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.ldp.containers.ContainerService;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class SesamePlatformAgentService extends SesameAgentsService {
	protected PlatformAgentRepository platformAgentRepository;

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
		ACL agentACL = aclRepository.createACL( agent.getIRI() );
		addAgentDefaultPermissions( agent, agentACL );

		if ( requireValidation ) {
			AgentValidator validator = createAgentValidator( agent );
			ACL validatorACL = aclRepository.createACL( validator.getIRI() );
			addValidatorDefaultPermissions( validatorACL );

			sendValidationEmail( agent, validator );
			// TODO: Create "resend validation" resource
		}
	}

	@Override
	public void create( IRI agentContainerIRI, Agent agent ) {
		validate( agent );
		String email = agent.getEmails().iterator().next();
		if ( platformAgentRepository.existsWithEmail( email ) ) throw new ResourceAlreadyExistsException();
		setAgentPasswordFields( agent );
		containerService.createChild( agentContainerIRI, agent );
	}

	private void addAgentDefaultPermissions( Agent agent, ACL agentACL ) {
		aclRepository.grantPermissions( agentACL, Arrays.asList( agent ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.UPDATE,
			ACEDescription.Permission.DELETE
		), false );
	}

	private void addAgentToDefaultPlatformRole( Agent agent ) {
		IRI defaultPlatformRoleIRI = getDefaultPlatformRoleIRI();
		IRI roleAgentsContainerIRI = getRoleAgentsContainerIRI( defaultPlatformRoleIRI );

		containerRepository.addMember( roleAgentsContainerIRI, agent.getIRI() );
	}

	private IRI getRoleAgentsContainerIRI( IRI defaultPlatformRoleIRI ) {
		// TODO: Use a Vars property
		return SimpleValueFactory.getInstance().createIRI( defaultPlatformRoleIRI.stringValue() + "agents/" );
	}

	private IRI getDefaultPlatformRoleIRI() {
		return Platform.Role.APP_DEVELOPER.getIRI();
	}

	@Autowired
	public void setPlatformAgentRepository( PlatformAgentRepository platformAgentRepository ) { this.platformAgentRepository = platformAgentRepository; }

}