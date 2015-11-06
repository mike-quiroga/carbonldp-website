package com.carbonldp.agents;

import com.carbonldp.agents.validators.AgentValidatorRepository;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.utils.AuthenticationUtil;
import freemarker.template.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

/**
 * @author NestorVenegas
 * @since 0.14.0_ALPHA
 */
public abstract class SesameAgentsService extends AbstractSesameLDPService implements AgentService {
	protected JavaMailSender mailSender;
	protected ConfigurationRepository configurationRepository;
	protected AgentValidatorRepository agentValidatorRepository;

	public SesameAgentsService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, AgentValidatorRepository agentValidatorRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );

		Assert.notNull( agentValidatorRepository );
		this.agentValidatorRepository = agentValidatorRepository;
	}

	@PostConstruct
	public void init() {
		Assert.notNull( mailSender );
		Assert.notNull( configurationRepository );
	}

	protected void validate( Agent agent ) {
		List<Infraction> infractions = AgentFactory.getInstance().validate( agent );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	protected void setAgentPasswordFields( Agent agent ) {
		String password = agent.getPassword();
		String salt = AuthenticationUtil.generateRandomSalt();
		agent.setSalt( salt );
		String saltedPassword = AuthenticationUtil.saltPassword( password, agent.getSalt() );
		String hashedPassword = AuthenticationUtil.hashPassword( saltedPassword );
		// TODO: use hashed password so it can be differentiated from the client sended one
		agent.setPassword( hashedPassword );
	}

	protected void addValidatorDefaultPermissions( ACL validatorACL ) {
		aclRepository.grantPermissions( validatorACL, Arrays.asList( Platform.Role.ANONYMOUS.asRDFResource() ), Arrays.asList(
			ACEDescription.Permission.READ
		), false );
	}

	protected void sendValidationEmail( Agent agent, AgentValidator validator ) {
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

	@Autowired
	public void setMailSender( JavaMailSender mailSender ) {
		Assert.notNull( mailSender );
		this.mailSender = mailSender;
	}

	// TODO: Store it in an RDFResource
	// TODO: Design the template
	private static final String emailTemplate;

	static {
		emailTemplate = "" +
			"Validate your email by clicking this link: <a link=\"${validatorURI}\">${validatorURI}</a>"
		;
	}

	private Map<String, Object> getEmailTemplateModel( Agent agent, AgentValidator validator ) {
		Map<String, Object> model = new HashMap<>();

		model.put( "validatorURI", validator.getURI() );
		// TODO: Finish

		return model;
	}

	protected AgentValidator createAgentValidator( Agent agent ) {
		AgentValidator validator = AgentValidatorFactory.getInstance().create( agent );
		agentValidatorRepository.create( validator );
		return validator;
	}

}
