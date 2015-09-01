package com.carbonldp.agents.platform;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentValidator;
import com.carbonldp.agents.AgentValidatorFactory;
import com.carbonldp.agents.validators.AgentValidatorRepository;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
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
import java.util.Map;

@Transactional
public class SesamePlatformAgentService extends AbstractSesameLDPService implements PlatformAgentService {
	private PlatformAgentRepository platformAgentRepository;
	private AgentValidatorRepository agentValidatorRepository;
	private JavaMailSender mailSender;
	private ConfigurationRepository configurationRepository;

	public SesamePlatformAgentService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, PlatformAgentRepository platformAgentRepository, AgentValidatorRepository agentValidatorRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );

		Assert.notNull( platformAgentRepository );
		this.platformAgentRepository = platformAgentRepository;

		Assert.notNull( agentValidatorRepository );
		this.agentValidatorRepository = agentValidatorRepository;
	}

	@PostConstruct
	public void init() {
		Assert.notNull( mailSender );
		Assert.notNull( configurationRepository );
	}

	@Override
	public void register( Agent agent ) {
		// TODO: Validate agent
		String email = agent.getEmails().iterator().next();
		if ( platformAgentRepository.existsWithEmail( email ) ) throw new ResourceAlreadyExistsException();

		setAgentPasswordFields( agent );

		boolean requireValidation = configurationRepository.requireAgentEmailValidation();

		if ( requireValidation ) agent.setEnabled( false );
		else agent.setEnabled( true );

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

	private void setAgentPasswordFields( Agent agent ) {
		String password = agent.getPassword();
		String salt = AuthenticationUtil.generateRandomSalt();
		String saltedPassword = AuthenticationUtil.saltPassword( password, salt );
		String hashedPassword = AuthenticationUtil.hashPassword( saltedPassword );

		agent.setSalt( salt );
		agent.setPassword( hashedPassword );
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

	private AgentValidator createAgentValidator( Agent agent ) {
		AgentValidator validator = AgentValidatorFactory.create( agent );
		agentValidatorRepository.create( validator );
		return validator;
	}

	private void addValidatorDefaultPermissions( ACL validatorACL ) {
		aclRepository.grantPermissions( validatorACL, Arrays.asList( Platform.Role.ANONYMOUS.asRDFResource() ), Arrays.asList(
			ACEDescription.Permission.READ
		), false );
	}

	private void sendValidationEmail( Agent agent, AgentValidator validator ) {
		String emailText = null;
		try {
			emailText = prepareEmailText( agent, validator );
		} catch ( IOException e ) {

		}

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper messageHelper = new MimeMessageHelper( message );

		String receiptEmail = agent.getEmails().stream().findFirst().get();
		// TODO: Make this configurable
		String senderEmail = "no-reply@carbonldp.com";

		// TODO: Implement correctly
		try {
			messageHelper.setTo( receiptEmail );
			messageHelper.setText( emailText, true );
			messageHelper.setFrom( senderEmail );
		} catch ( MessagingException e ) {
			throw new StupidityException( e );
		}

		mailSender.send( message );
	}

	// TODO: Store it in an RDFResource
	// TODO: Design the template
	private static final String emailTemplate;

	static {
		emailTemplate = "" +
			"Validate your email by clicking this link: <a link=\"${validatorURI}\">${validatorURI}</a>"
		;
	}

	private String prepareEmailText( Agent agent, AgentValidator validator ) throws IOException {
		// TODO: Make this a singleton, creating it each time is expensive
		// TODO: Make the version a constant
		Version freemarkerVersion = new Version( "2.3.22" );
		Configuration templateConfiguration = new Configuration( freemarkerVersion );
		DefaultObjectWrapperBuilder objectWrapperBuilder = new DefaultObjectWrapperBuilder( freemarkerVersion );
		templateConfiguration.setObjectWrapper( objectWrapperBuilder.build() );

		// TODO: Cache the template, parsing the string over and over again is expensive
		Template template = new Template( "email-validation", emailTemplate, templateConfiguration );

		Map<String, Object> model = getEmailTemplateModel( agent, validator );

		String emailText = null;
		try {
			emailText = FreeMarkerTemplateUtils.processTemplateIntoString( template, model );
		} catch ( TemplateException e ) {
			throw new StupidityException( e );
		}

		return emailText;
	}

	private Map<String, Object> getEmailTemplateModel( Agent agent, AgentValidator validator ) {
		Map<String, Object> model = new HashMap<>();

		model.put( "validatorURI", validator.getURI() );
		// TODO: Finish

		return model;
	}

	@Autowired
	public void setMailSender( JavaMailSender mailSender ) {
		Assert.notNull( mailSender );
		this.mailSender = mailSender;
	}

	@Autowired
	public void setConfigurationRepository( ConfigurationRepository configurationRepository ) {
		Assert.notNull( configurationRepository );
		this.configurationRepository = configurationRepository;
	}

}