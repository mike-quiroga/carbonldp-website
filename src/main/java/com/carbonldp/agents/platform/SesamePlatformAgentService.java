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
import com.carbonldp.rdf.*;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Set;

public class SesamePlatformAgentService extends SesameAgentsService {
	protected PlatformAgentRepository platformAgentRepository;
	protected AppService appService;
	protected AppRoleRepository appRoleRepository;
	protected RDFResourceRepository resourceRepository;
	protected RDFMapRepository mapRepository;

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
		createAppRoleMap( agent );

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
		createAppRoleMap( agent );
	}

	@Override
	public void delete( IRI agentIRI ) {
		if ( ! sourceRepository.exists( agentIRI ) ) return;
		Agent agentResource = platformAgentRepository.get( agentIRI );
		IRI appRoleMapIRI = agentResource.getIRI( PlatformAgentDescription.Property.APP_ROLE_MAP );
		if ( agentIRI.stringValue().equals( Vars.getInstance().getPlatformAgentSystemURL() ) )
			throw new BadRequestException( new Infraction( 0x2014 ) );
		Set<Value> apps = mapRepository.getKeys( appRoleMapIRI );
		for ( Value app : apps ) {
			validateDeletePlatformAgent( ValueUtil.getIRI( app ), appRoleMapIRI );
		}
		sourceRepository.delete( appRoleMapIRI, true );
		sourceRepository.delete( agentIRI, true );
	}

	private void validateDeletePlatformAgent( IRI app, IRI rdfMapIRI ) {
		IRI appIRI = SimpleValueFactory.getInstance().createIRI( app.stringValue() );
		App appResource = appService.get( appIRI );
		Set<Value> roles = mapRepository.getValues( rdfMapIRI, app );
		transactionWrapper.runInAppContext( appResource, () -> {
			String adminRoleString = appRoleRepository.getContainerIRI() + Vars.getInstance().getAppAdminRole();
			for ( Value role : roles ) {
				validateIsTheOnlyAdmin( ValueUtil.getIRI( role ), adminRoleString, appIRI );
			}
		} );
	}

	private void validateIsTheOnlyAdmin( IRI role, String adminRoleString, IRI appIRI ) {
		if ( ! role.stringValue().equals( adminRoleString ) ) return;
		Set<IRI> adminAgentsIRIs = containerRepository.getMemberIRIs( SimpleValueFactory.getInstance().createIRI( adminRoleString + Vars.getInstance().getAgentsContainer() ) );
		if ( adminAgentsIRIs.size() > 1 ) return;
		throw new BadRequestException( new Infraction( 0x2013, "app", appIRI.stringValue() ) );

	}

	public void createAppRoleMap( Agent agent ) {
		ValueFactory valueFactory = SimpleValueFactory.getInstance();
		IRI appRoleMapIRI = valueFactory.createIRI( agent.getIRI().stringValue() + Vars.getInstance().getAppRoleMap() );
		RDFMap map = RDFMapFactory.getInstance().create( appRoleMapIRI );
		containerRepository.createChild( agent.getIRI(), map );
		resourceRepository.add( agent.getIRI(), PlatformAgentDescription.Property.APP_ROLE_MAP.getIRI(), map.getIRI() );

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

	@Autowired
	public void setResourceRepository( RDFResourceRepository resourceRepository ) {
		this.resourceRepository = resourceRepository;
	}

	@Autowired
	public void setMapRepository( RDFMapRepository mapRepository ) {
		this.mapRepository = mapRepository;
	}
}