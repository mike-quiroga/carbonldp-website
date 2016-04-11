package com.carbonldp.apps.roles;

import com.carbonldp.agents.AgentDescription;
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
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

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
	public boolean exists( URI appRoleURI ) {
		return appRoleRepository.exists( appRoleURI );
	}

	@Override
	public void addAgents( URI appRoleAgentContainerURI, Set<URI> agents ) {
		for ( URI agent : agents ) {
			addAgent( appRoleAgentContainerURI, agent );
		}
	}

	@Override
	public void addAgent( URI appRoleAgentContainerURI, URI agent ) {
		if ( ( ! sourceRepository.exists( appRoleAgentContainerURI ) ) ) throw new ResourceDoesntExistException();
		if ( ! isAppAgent( agent ) && ! isPlatformAgent( agent ) ) throw new InvalidRDFTypeException( new Infraction( 0x2001, "rdf.type", AgentDescription.Resource.CLASS.getIRI().stringValue() ) );

		containerService.addMember( appRoleAgentContainerURI, agent );

		DateTime modifiedTime = DateTime.now();
		URI membershipResource = containerRepository.getTypedRepository( containerService.getContainerType( appRoleAgentContainerURI ) ).getMembershipResource( appRoleAgentContainerURI );
		sourceRepository.touch( membershipResource, modifiedTime );
	}

	public void create( AppRole appRole ) {
		if ( sourceRepository.exists( appRole.getIRI() ) ) throw new ResourceAlreadyExistsException();
		validate( appRole );

		containerService.createChild( appRoleRepository.getContainerURI(), appRole );
		createAgentsContainer( appRole );
	}

	@Override
	public void addChildren( URI parentRole, Set<URI> childs ) {
		for ( URI member : childs ) {
			addChild( parentRole, member );
		}
	}

	@Override
	public void addChild( URI parentRoleURI, URI child ) {
		if ( ( ! sourceRepository.exists( parentRoleURI ) ) || ( ! sourceRepository.exists( child ) ) ) throw new ResourceDoesntExistException();
		if ( ! sourceRepository.is( child, AppRoleDescription.Resource.CLASS ) ) throw new InvalidResourceException( new Infraction( 0x2001, "rdf.type", AppRoleDescription.Resource.CLASS.getIRI().stringValue() ) );

		validateHasParent( child );
		containerService.addMember( parentRoleURI, child );

		DateTime modifiedTime = DateTime.now();
		sourceRepository.touch( parentRoleURI, modifiedTime );

	}

	@Override
	public void delete( URI appRoleURI ) {
		if ( ! exists( appRoleURI ) ) throw new ResourceDoesntExistException();
		appRoleRepository.delete( appRoleURI );
	}

	private void validate( AppRole appRole ) {
		List<Infraction> infractions = AppRoleFactory.getInstance().validate( appRole );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private boolean isAppAgent( URI agent ) {
		return isAgent( agent );
	}

	private boolean isAgent( URI agent ) {
		if ( sourceRepository.exists( agent ) ) {
			if ( sourceRepository.is( agent, AgentDescription.Resource.CLASS ) ) return true;
			else throw new InvalidRDFTypeException( new Infraction( 0x2001, "rdf.type", AgentDescription.Resource.CLASS.getIRI().stringValue() ) );
		}
		return false;
	}

	private boolean isPlatformAgent( URI agent ) {
		return platformAgentRepository.exists( agent );
	}

	private void createAgentsContainer( AppRole appRole ) {
		URI agentsContainerURI = appRoleRepository.getAgentsContainerURI( appRole.getIRI() );
		RDFResource resource = new RDFResource( agentsContainerURI );
		DirectContainer container = DirectContainerFactory.getInstance().create( resource, appRole.getIRI(), AppRoleDescription.Property.AGENT.getIRI() );
		sourceRepository.createAccessPoint( appRole.getIRI(), container );
		aclRepository.createACL( container.getIRI() );
	}

	private void validateHasParent( URI childURI ) {
		if ( ! sourceRepository.exists( childURI ) ) throw new ResourceDoesntExistException();
		Set<URI> parentsRoles = appRoleRepository.getParentsURI( childURI );
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
