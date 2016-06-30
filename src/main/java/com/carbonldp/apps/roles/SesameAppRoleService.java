package com.carbonldp.apps.roles;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentDescription;
import com.carbonldp.agents.PlatformAgentDescription;
import com.carbonldp.agents.platform.PlatformAgentRepository;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.AppRoleDescription;
import com.carbonldp.apps.AppRoleFactory;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.exceptions.*;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.*;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFMap;
import com.carbonldp.rdf.RDFResource;
import org.joda.time.DateTime;
import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since 0.18.0-ALPHA
 */

public class SesameAppRoleService extends AbstractSesameLDPService implements AppRoleService {

	protected RDFSourceService sourceService;
	protected ContainerService containerService;

	protected PlatformAgentRepository platformAgentRepository;
	protected AppRoleRepository appRoleRepository;

	@Override
	public boolean exists( IRI appRoleIRI ) {
		return appRoleRepository.exists( appRoleIRI );
	}

	@Override
	public void addAgents( IRI appRoleAgentContainerIRI, Collection<IRI> agents ) {
		for ( IRI agent : agents ) {
			addAgent( appRoleAgentContainerIRI, agent );
		}
	}

	@Override
	public void addAgent( IRI appRoleAgentContainerIRI, IRI agent ) {
		if ( ( ! sourceRepository.exists( appRoleAgentContainerIRI ) ) ) throw new ResourceDoesntExistException();
		boolean isPlatformAgent = isPlatformAgent( agent );
		if ( ! isAppAgent( agent ) && ! isPlatformAgent ) throw new InvalidRDFTypeException( new Infraction( 0x2001, "rdf.type", AgentDescription.Resource.CLASS.getIRI().stringValue() ) );

		containerService.addMember( appRoleAgentContainerIRI, agent );

		if ( isPlatformAgent ) {
			Container accessPoint = AccessPointFactory.getInstance().getAccessPoint( sourceService.get( appRoleAgentContainerIRI ) );
			IRI roleIRI = accessPoint.getMembershipResource();
			IRI appIRI = AppContextHolder.getContext().getApplication().getIRI();
			transactionWrapper.runInPlatformContext( () -> {
				Agent agentResource = platformAgentRepository.get( agent );
				BNode rdfMapBNode = agentResource.getBNode( PlatformAgentDescription.Property.APP_ROLE_MAP );
				RDFMap map = new RDFMap( agentResource.getBaseModel(), rdfMapBNode, agent );
				map.add( (Value) appIRI, (Value) roleIRI );
				agentResource.set( PlatformAgentDescription.Property.APP_ROLE_MAP.getIRI(), rdfMapBNode );
				sourceService.replace( agentResource );
			} );
		}

		DateTime modifiedTime = DateTime.now();
		IRI membershipResource = containerRepository.getTypedRepository( containerService.getContainerType( appRoleAgentContainerIRI ) ).getMembershipResource( appRoleAgentContainerIRI );
		sourceRepository.touch( membershipResource, modifiedTime );
	}

	@Override
	public void removeAgents( IRI appRoleAgentContainerIRI ) {
		Set<IRI> agents = containerRepository.getMemberIRIs( appRoleAgentContainerIRI );
		for ( IRI agent : agents ) {
			removeAgent( appRoleAgentContainerIRI, agent );
		}
	}

	@Override
	public void removeAgents( IRI appRoleAgentContainerIRI, Collection<IRI> agents ) {
		for ( IRI agent : agents ) {
			removeAgent( appRoleAgentContainerIRI, agent );
		}
	}

	@Override
	public void removeAgent( IRI appRoleAgentContainerIRI, IRI agent ) {
		if ( ( ! sourceRepository.exists( appRoleAgentContainerIRI ) ) ) throw new ResourceDoesntExistException();
		boolean isPlatformAgent = isPlatformAgent( agent );
		if ( ! isAppAgent( agent ) && ! isPlatformAgent ) throw new InvalidRDFTypeException( new Infraction( 0x2001, "rdf.type", AgentDescription.Resource.CLASS.getIRI().stringValue() ) );

		containerService.removeMember( appRoleAgentContainerIRI, agent );

		if ( isPlatformAgent ) {
			Container accessPoint = AccessPointFactory.getInstance().getAccessPoint( sourceService.get( appRoleAgentContainerIRI ) );
			IRI roleIRI = accessPoint.getMembershipResource();
			IRI appIRI = AppContextHolder.getContext().getApplication().getIRI();
			transactionWrapper.runInPlatformContext( () -> {
				Agent agentResource = platformAgentRepository.get( agent );
				BNode rdfMapBNode = agentResource.getBNode( PlatformAgentDescription.Property.APP_ROLE_MAP );
				if ( rdfMapBNode == null ) return;
				RDFMap map = new RDFMap( agentResource.getBaseModel(), rdfMapBNode, agent );
				map.remove( (Value) appIRI, (Value) roleIRI );
				agentResource.set( PlatformAgentDescription.Property.APP_ROLE_MAP.getIRI(), rdfMapBNode );
				sourceService.replace( agentResource );
			} );
		}

		DateTime modifiedTime = DateTime.now();
		IRI membershipResource = containerRepository.getTypedRepository( containerService.getContainerType( appRoleAgentContainerIRI ) ).getMembershipResource( appRoleAgentContainerIRI );
		sourceRepository.touch( membershipResource, modifiedTime );
	}

	@Override
	public void create( AppRole appRole ) {
		if ( sourceRepository.exists( appRole.getIRI() ) ) throw new ResourceAlreadyExistsException();
		validate( appRole );

		containerService.createChild( appRoleRepository.getContainerIRI(), appRole );
		createAgentsContainer( appRole );
	}

	@Override
	public void addChildren( IRI parentRole, Set<IRI> children ) {
		for ( IRI member : children ) {
			addChild( parentRole, member );
		}
	}

	@Override
	public void addChild( IRI parentRoleIRI, IRI child ) {
		if ( ( ! sourceRepository.exists( parentRoleIRI ) ) || ( ! sourceRepository.exists( child ) ) ) throw new ResourceDoesntExistException();
		if ( ! sourceRepository.is( child, AppRoleDescription.Resource.CLASS ) )
			throw new InvalidResourceException( new Infraction( 0x2001, "rdf.type", AppRoleDescription.Resource.CLASS.getIRI().stringValue() ) );

		validateHasParent( child );
		containerService.addMember( parentRoleIRI, child );

		DateTime modifiedTime = DateTime.now();
		sourceRepository.touch( parentRoleIRI, modifiedTime );

	}

	@Override
	public void delete( IRI appRoleIRI ) {
		if ( ! exists( appRoleIRI ) ) throw new ResourceDoesntExistException();
		appRoleRepository.delete( appRoleIRI );
	}

	@Override
	public IRI getAgentsContainerIRI( IRI appRoleIRI ) {
		return appRoleRepository.getAgentsContainerIRI( appRoleIRI );
	}

	private void validate( AppRole appRole ) {
		List<Infraction> infractions = AppRoleFactory.getInstance().validate( appRole );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private boolean isAppAgent( IRI agent ) {
		return isAgent( agent );
	}

	private boolean isAgent( IRI agent ) {
		if ( sourceRepository.exists( agent ) ) {
			if ( sourceRepository.is( agent, AgentDescription.Resource.CLASS ) ) return true;
			else throw new InvalidRDFTypeException( new Infraction( 0x2001, "rdf.type", AgentDescription.Resource.CLASS.getIRI().stringValue() ) );
		}
		return false;
	}

	private boolean isPlatformAgent( IRI agent ) {
		return platformAgentRepository.exists( agent );
	}

	private void createAgentsContainer( AppRole appRole ) {
		IRI agentsContainerIRI = appRoleRepository.getAgentsContainerIRI( appRole.getIRI() );
		RDFResource resource = new RDFResource( agentsContainerIRI );
		DirectContainer container = DirectContainerFactory.getInstance().create( resource, appRole.getIRI(), AppRoleDescription.Property.AGENT.getIRI() );
		sourceRepository.createAccessPoint( appRole.getIRI(), container );
		aclRepository.createACL( container.getIRI() );
	}

	private void validateHasParent( IRI childIRI ) {
		if ( ! sourceRepository.exists( childIRI ) ) throw new ResourceDoesntExistException();
		Set<IRI> parentsRoles = appRoleRepository.getParentsIRI( childIRI );
		if ( ! parentsRoles.isEmpty() ) throw new AlreadyHasAParentException();
	}

	@Autowired
	public void setRDFSourceService( RDFSourceService sourceService ) { this.sourceService = sourceService; }

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }

	@Autowired
	public void setPlatformAgentRepository( PlatformAgentRepository platformAgentRepository ) { this.platformAgentRepository = platformAgentRepository; }

	@Autowired
	public void setAppRoleRepository( AppRoleRepository appRoleRepository ) { this.appRoleRepository = appRoleRepository; }
}
