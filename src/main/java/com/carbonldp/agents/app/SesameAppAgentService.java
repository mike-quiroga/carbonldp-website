package com.carbonldp.agents.app;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.agents.AgentValidator;
import com.carbonldp.agents.SesameAgentsService;
import com.carbonldp.agents.validators.AgentValidatorRepository;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author NestorVenegas
 * @since 0.14.0_ALPHA
 */

@Transactional
public class SesameAppAgentService extends SesameAgentsService {

	private AgentRepository appAgentRepository;
	private AppRoleRepository appRoleRepository;

	public SesameAppAgentService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, AgentRepository appAgentRepository, AgentValidatorRepository agentValidatorRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository, agentValidatorRepository );

		Assert.notNull( appAgentRepository );
		this.appAgentRepository = appAgentRepository;
	}

	@Override
	public void register( Agent agent ) {
		validate( agent );

		String email = agent.getEmails().iterator().next();
		if ( appAgentRepository.existsWithEmail( email ) ) throw new ResourceAlreadyExistsException();
		setAgentPasswordFields( agent );

		boolean requireValidation = configurationRepository.requireAgentEmailValidation();
		if ( requireValidation ) agent.setEnabled( false );
		else agent.setEnabled( true );

		appAgentRepository.create( agent );

		if ( requireValidation ) {
			AgentValidator validator = createAgentValidator( agent );
			ACL validatorACL = aclRepository.createACL( validator.getDocument() );
			addValidatorDefaultPermissions( validatorACL );

			sendValidationEmail( agent, validator );
			// TODO: Create "resend validation" resource
		}

	}

	@Autowired
	public void setConfigurationRepository( ConfigurationRepository configurationRepository ) {
		Assert.notNull( configurationRepository );
		this.configurationRepository = configurationRepository;
	}

	@Autowired
	public void setAppRoleRepository( AppRoleRepository appRoleRepository ) {
		this.appRoleRepository = appRoleRepository;
	}
}
