package com.carbonldp.apps.roles;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentDescription;
import com.carbonldp.agents.PlatformAgentDescription;
import com.carbonldp.agents.platform.PlatformAgentRepository;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.AppRoleDescription;
import com.carbonldp.apps.AppRoleFactory;
import com.carbonldp.exceptions.*;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.containers.DirectContainer;
import com.carbonldp.ldp.containers.DirectContainerFactory;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResource;
import org.joda.time.DateTime;
import org.openrdf.model.IRI;
import org.openrdf.model.Resource;
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
		if ( ! isAppAgent( agent ) && ! isPlatformAgent( agent ) ) throw new InvalidRDFTypeException( new Infraction( 0x2001, "rdf.type", AgentDescription.Resource.CLASS.getIRI().stringValue() ) );

		containerService.addMember( appRoleAgentContainerIRI, agent );

		if ( isPlatformAgent( agent ) ) {
			transactionWrapper.runInPlatformContext( () -> {
				Agent agentResource = platformAgentRepository.get( agent );
				Resource rdfMap = agentResource.getResource( PlatformAgentDescription.Property.APP_ROLE_MAP );
				//rdfMap.add( AppContextHolder.getContext().getApplication().getIRI(), appRoleRepository.getParentsIRI( appRoleAgentContainerIRI ).iterator().next() );

				//TODO: fix this
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
	public void addChildren( IRI parentRole, Set<IRI> childs ) {
		for ( IRI member : childs ) {
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
