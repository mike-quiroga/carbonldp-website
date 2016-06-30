package com.carbonldp.agents.platform;

import com.carbonldp.Vars;
import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentValidator;
import com.carbonldp.agents.PlatformAgentDescription;
import com.carbonldp.agents.SesameAgentsService;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppService;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFMap;
import com.carbonldp.rdf.RDFMapDescription;
import com.carbonldp.web.exceptions.BadRequestException;
import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Set;

public class SesamePlatformAgentService extends SesameAgentsService {
	protected PlatformAgentRepository platformAgentRepository;
	protected AppService appService;
	protected AppRoleRepository appRoleRepository;

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
		createAppRoleMap( agent );

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
	public void delete( IRI agentIRI ) {
		if ( sourceRepository.exists( agentIRI ) ) {
			Agent agentResource = platformAgentRepository.get( agentIRI );
			BNode rdfMapBNode = agentResource.getBNode( PlatformAgentDescription.Property.APP_ROLE_MAP );
			if ( rdfMapBNode == null ) return;
			RDFMap map = new RDFMap( agentResource.getBaseModel(), rdfMapBNode, agentIRI );
			Set<Value> apps = map.getKeys();
			for ( Value app : apps ) {
				IRI appIRI = SimpleValueFactory.getInstance().createIRI( app.stringValue() );
				App appResource = appService.get( appIRI );
				String adminRoleString = transactionWrapper.runInAppContext( appResource, () -> {
					return appRoleRepository.getContainerIRI() + Vars.getInstance().getAppAdminRole();
				} );
				Set<Value> roles = map.getValues( app );
				for ( Value role : roles ) {
					if ( role.stringValue().equals( adminRoleString ) ) {
						throw new BadRequestException( new Infraction( 0x2013, "app", appIRI.stringValue() ) );
					}
				}
			}
		}
		sourceRepository.delete( agentIRI, true );
	}

	public void createAppRoleMap( Agent agent ) {
		BNode mapBNode = SimpleValueFactory.getInstance().createBNode();
		RDFMap map = new RDFMap( agent.getBaseModel(), mapBNode, agent.getIRI() );
		map.addType( RDFMapDescription.Resource.CLASS.getIRI() );
		agent.add( PlatformAgentDescription.Property.APP_ROLE_MAP.getIRI(), mapBNode );
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

	@Autowired
	public void setAppService( AppService appService ) { this.appService = appService; }

	@Autowired
	public void setAppRoleRepository( AppRoleRepository appRoleRepository ) { this.appRoleRepository = appRoleRepository; }
}