package com.carbonldp.agents.app;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentValidator;
import com.carbonldp.agents.SesameAgentsService;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.utils.ModelUtil;
import com.carbonldp.utils.RDFDocumentUtil;
import org.openrdf.model.IRI;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Collectors;

/**
 * @author NestorVenegas
 * @since 0.14.0-ALPHA
 */

public class SesameAppAgentService extends SesameAgentsService {

	protected AppAgentRepository appAgentRepository;
	protected AppRoleRepository appRoleRepository;
	protected RDFSourceService sourceService;

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
		aclRepository.createACL( agent.getIRI() );

		if ( requireValidation ) {
			AgentValidator validator = createAgentValidator( agent );
			ACL validatorACL = aclRepository.createACL( validator.getIRI() );
			addValidatorDefaultPermissions( validatorACL );

			sendValidationEmail( agent, validator );
			// TODO: Create "resend validation" resource
		}

	}

	public void replace( IRI source, Agent agent ) {
		RDFSource originalSource = sourceService.get( agent.getIRI() );
		RDFDocument originalDocument = originalSource.getDocument();

		if ( agent.getPassword().length() != 64 ) {
			setAgentPasswordFields( agent );
		}

		RDFDocument newDocument = RDFDocumentUtil.mapBNodeSubjects( originalDocument, agent.getDocument() );

		AbstractModel toAdd = newDocument.stream().filter( statement -> ! ModelUtil.containsStatement( originalDocument, statement ) ).collect( Collectors.toCollection( LinkedHashModel::new ) );
		RDFDocument documentToAdd = new RDFDocument( toAdd, source );

		AbstractModel toDelete = originalDocument.stream().filter( statement -> ! ModelUtil.containsStatement( newDocument, statement ) ).collect( Collectors.toCollection( LinkedHashModel::new ) );
		RDFDocument documentToDelete = new RDFDocument( toDelete, source );

		sourceService.replace( originalSource.getIRI(), documentToAdd, documentToDelete );
	}

	@Autowired
	public void setAppAgentRepository( AppAgentRepository appAgentRepository ) { this.appAgentRepository = appAgentRepository; }

	@Autowired
	public void setAppRoleRepository( AppRoleRepository appRoleRepository ) { this.appRoleRepository = appRoleRepository; }

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) {
		this.sourceService = sourceService;
	}
}
