package com.carbonldp.agents.platform;

import com.carbonldp.agents.*;
import com.carbonldp.agents.validators.AgentValidatorRepository;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.AuthenticationUtil;
import freemarker.template.*;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		setSalt( agent );
		boolean requireValidation = configurationRepository.requireAgentEmailValidation();
		if ( requireValidation ) agent.setEnabled( false );
		else agent.setEnabled( true );
		validate( agent );

		String email = agent.getEmails().iterator().next();
		if ( platformAgentRepository.existsWithEmail( email ) ) throw new ResourceAlreadyExistsException();

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