package com.carbonldp.agents.platform;

import com.carbonldp.Vars;
import com.carbonldp.agents.*;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppService;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFMap;
import com.carbonldp.rdf.RDFMapFactory;
import com.carbonldp.rdf.RDFMapRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.repository.Query;

import java.util.Arrays;
import java.util.Set;

public class SesamePlatformAgentService extends SesameAgentsService {
	protected AppService appService;
	protected AppRoleRepository appRoleRepository;
	protected RDFResourceRepository resourceRepository;
	protected RDFMapRepository mapRepository;

	@Override
	public void register( Agent agent ) {
		super.register( agent );
		createAppRoleMap( agent );
	}

	@Override
	public Agent get( IRI agentIRI ) {
		return agentRepository.get( agentIRI );
	}

	@Override
	public void create( IRI agentContainerIRI, Agent agent ) {
		super.create( agentContainerIRI, agent );
		createAppRoleMap( agent );
	}

	@Override
	public void delete( IRI agentIRI ) {
		if ( ! sourceRepository.exists( agentIRI ) ) return;
		Agent agentResource = agentRepository.get( agentIRI );
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

	protected void addAgentToDefaultRole( Agent agent ) {
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
	public void setPlatformAgentRepository( PlatformAgentRepository agentRepository ) { this.agentRepository = agentRepository; }

	@Autowired
	public void setAppService( AppService appService ) { this.appService = appService; }

	@Autowired
	public void setAppRoleRepository( AppRoleRepository appRoleRepository ) { this.appRoleRepository = appRoleRepository; }

	@Autowired
	public void setResourceRepository( RDFResourceRepository resourceRepository ) { this.resourceRepository = resourceRepository; }

	@Autowired
	public void setMapRepository( RDFMapRepository mapRepository ) { this.mapRepository = mapRepository; }
}