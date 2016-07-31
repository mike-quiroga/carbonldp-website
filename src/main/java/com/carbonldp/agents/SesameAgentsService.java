package com.carbonldp.agents;

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
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.utils.AuthenticationUtil;
import com.carbonldp.utils.ModelUtil;
import com.carbonldp.utils.RDFDocumentUtil;
import freemarker.template.*;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.impl.AbstractModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public abstract class SesameAgentsService extends AbstractSesameLDPService implements AgentService {

	protected ConfigurationRepository configurationRepository;
	protected AgentValidatorRepository agentValidatorRepository;
	protected RDFSourceService sourceService;
	protected ContainerService containerService;
	protected AgentRepository agentRepository;
	protected ACLRepository aclRepository;

	protected JavaMailSender mailSender;

	@Override
	public void register( Agent agent ) {

		validate( agent );

		String email = agent.getEmails().iterator().next();
		if ( agentRepository.existsWithEmail( email ) ) throw new ResourceAlreadyExistsException();
		setAgentPasswordFields( agent );

		boolean requireValidation = configurationRepository.requireAgentEmailValidation();
		if ( requireValidation ) agent.setEnabled( false );
		else agent.setEnabled( true );

		addAgentToDefaultRole( agent );

		agentRepository.create( agent );
		aclRepository.createACL( agent.getIRI() );
		addAgentDefaultPermissions( agent );

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
		if ( agentRepository.existsWithEmail( email ) ) throw new ResourceAlreadyExistsException();
		setAgentPasswordFields( agent );
		addAgentToDefaultRole( agent );

		containerService.createChild( agentContainerIRI, agent );
		addAgentDefaultPermissions( agent );
	}

	@Override
	public void replace( IRI source, Agent agent ) {
		validateNumberOfPasswordAndEmails( agent );
		RDFSource originalSource = sourceService.get( agent.getIRI() );
		RDFDocument originalDocument = originalSource.getDocument();

		RDFDocument newDocument = RDFDocumentUtil.mapBNodeSubjects( originalDocument, agent.getDocument() );

		AbstractModel toAdd = newDocument.stream().filter( statement -> ! ModelUtil.containsStatement( originalDocument, statement ) ).collect( Collectors.toCollection( LinkedHashModel::new ) );
		RDFDocument documentToAdd = new RDFDocument( toAdd, source );
		AbstractModel toDelete = originalDocument.stream().filter( statement -> ! ModelUtil.containsStatement( newDocument, statement ) ).collect( Collectors.toCollection( LinkedHashModel::new ) );

		RDFDocument documentToDelete = new RDFDocument( toDelete, source );
		if ( toDelete.contains( null, AgentDescription.Property.PASSWORD.getIRI(), null ) ) {
			Model statement = originalDocument.filter( null, AgentDescription.Property.SALT.getIRI(), null );
			documentToDelete.addAll( statement );
		}
		Agent agentToAdd = new Agent( documentToAdd, agent.getIRI() );
		if ( agentToAdd.getPassword() != null ) setAgentPasswordFields( agentToAdd );

		sourceService.patch( originalSource.getIRI(), documentToAdd, documentToDelete );
	}

	protected void addAgentDefaultPermissions( Agent agent ) {
		aclRepository.grantPermissions( agent.getIRI(), Arrays.asList( agent ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.UPDATE,
			ACEDescription.Permission.DELETE
		), false );
	}

	protected void addAgentToDefaultRole( Agent agent ) {}

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
			throw new RuntimeException( e );
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

	private void validateNumberOfPasswordAndEmails( Agent agent ) {
		Set<Value> passwords = agent.getProperties( AgentDescription.Property.PASSWORD );
		if ( passwords.size() > 1 ) throw new InvalidResourceException( new Infraction( 0x2004, "property", AgentDescription.Property.PASSWORD.getIRI().stringValue() ) );

		Set<Value> emails = agent.getProperties( AgentDescription.Property.EMAIL );
		if ( emails.size() < 1 ) throw new InvalidResourceException( new Infraction( 0x2004, "property", AgentDescription.Property.PASSWORD.getIRI().stringValue() ) );
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

		String emailText;
		try {
			emailText = FreeMarkerTemplateUtils.processTemplateIntoString( template, model );
		} catch ( TemplateException e ) {
			throw new StupidityException( e );
		}

		return emailText;
	}

	// TODO: Store it in an RDFResource
	// TODO: Design the template
	private static final String emailTemplate;

	static {
		emailTemplate = "" +
			"Validate your email by clicking this link: <a link=\"${validatorIRI}\">${validatorIRI}</a>"
		;
	}

	private Map<String, Object> getEmailTemplateModel( Agent agent, AgentValidator validator ) {
		Map<String, Object> model = new HashMap<>();

		model.put( "validatorIRI", validator.getIRI() );
		// TODO: Finish

		return model;
	}

	protected AgentValidator createAgentValidator( Agent agent ) {
		AgentValidator validator = AgentValidatorFactory.getInstance().create( agent );
		agentValidatorRepository.create( validator );
		return validator;
	}

	@Autowired
	public void setConfigurationRepository( ConfigurationRepository configurationRepository ) { this.configurationRepository = configurationRepository; }

	@Autowired
	public void setAgentValidatorRepository( AgentValidatorRepository agentValidatorRepository ) { this.agentValidatorRepository = agentValidatorRepository; }

	@Autowired
	public void setMailSender( JavaMailSender mailSender ) { this.mailSender = mailSender; }

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) { this.sourceService = sourceService; }

	@Autowired
	public void setContainerService( ContainerService containerService ) {
		this.containerService = containerService;
	}

	@Autowired
	public void setAclRepository( ACLRepository aclRepository ) {
		this.aclRepository = aclRepository;
	}
}
