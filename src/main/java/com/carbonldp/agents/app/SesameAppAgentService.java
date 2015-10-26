package com.carbonldp.agents.app;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentFactory;
import com.carbonldp.agents.validators.AgentValidatorRepository;
import com.carbonldp.apps.App;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.AuthenticationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author NestorVenegas
 * @since _version_
 */

@Transactional
public class SesameAppAgentService extends AbstractSesameLDPService implements AppAgentService {

	private AppAgentRepository appAgentRepository;
	private AgentValidatorRepository agentValidatorRepository;
	private AppRoleRepository appRoleRepository;
	private ConfigurationRepository configurationRepository;

	public SesameAppAgentService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, AppAgentRepository appAgentRepository, AgentValidatorRepository agentValidatorRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );

		Assert.notNull( appAgentRepository );
		this.appAgentRepository = appAgentRepository;

		Assert.notNull( agentValidatorRepository );
		this.agentValidatorRepository = agentValidatorRepository;
	}

	@Override
	public void register( App app, Agent agent ) {
		String email = agent.getEmails().iterator().next();
		transactionWrapper.runInAppcontext( app, () -> {
			if ( appAgentRepository.existsWithEmail( email ) ) throw new ResourceAlreadyExistsException();
			setAgentPasswordFields( agent );

			boolean requireValidation = configurationRepository.requireAgentEmailValidation();
			if ( requireValidation ) agent.setEnabled( false );
			else agent.setEnabled( true );
			validate( agent );

			appAgentRepository.create( app.getRootContainerURI(), agent );
		} );

	}

	private void validate( Agent agent ) {
		List<Infraction> infractions = AgentFactory.getInstance().validate( agent );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private void setAgentPasswordFields( Agent agent ) {
		String password = agent.getPassword();
		String salt = AuthenticationUtil.generateRandomSalt();
		String saltedPassword = AuthenticationUtil.saltPassword( password, salt );
		String hashedPassword = AuthenticationUtil.hashPassword( saltedPassword );

		agent.setSalt( salt );
		agent.setPassword( hashedPassword );
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
