package com.carbonldp.apps.roles;

import com.carbonldp.agents.AgentDescription;
import com.carbonldp.agents.platform.PlatformAgentRepository;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.AppRoleDescription;
import com.carbonldp.apps.AppRoleFactory;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.exceptions.*;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.containers.DirectContainer;
import com.carbonldp.ldp.containers.DirectContainerFactory;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.spring.TransactionWrapper;
import org.joda.time.DateTime;
import org.openrdf.model.URI;

import java.util.List;
import java.util.Set;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since 0.18.0-ALPHA
 */

public class SesameAppRoleService extends AbstractSesameLDPService implements AppRoleService {

	private final ContainerService containerService;
	private final AppRoleRepository appRoleRepository;
	private final RDFSourceService sourceService;
	protected final PlatformAgentRepository platformAgentRepository;

	public SesameAppRoleService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, ContainerService containerService, AppRoleRepository appRoleRepository, RDFSourceService sourceService, PlatformAgentRepository platformAgentRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		this.appRoleRepository = appRoleRepository;
		this.containerService = containerService;
		this.sourceService = sourceService;
		this.platformAgentRepository = platformAgentRepository;
	}

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
		if ( ! isAppAgent( agent ) && ! isPlatformAgent( agent ) ) throw new InvalidRDFTypeException( new Infraction( 0x2001, "rdf.type", AgentDescription.Resource.CLASS.getURI().stringValue() ) );

		containerService.addMember( appRoleAgentContainerURI, agent );

		DateTime modifiedTime = DateTime.now();
		URI membershipResource = containerRepository.getTypedRepository( containerService.getContainerType( appRoleAgentContainerURI ) ).getMembershipResource( appRoleAgentContainerURI );
		sourceRepository.touch( membershipResource, modifiedTime );
	}

	public void create( AppRole appRole ) {
		if ( sourceRepository.exists( appRole.getURI() ) ) throw new ResourceAlreadyExistsException();
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
		if ( ! sourceRepository.is( child, AppRoleDescription.Resource.CLASS ) ) throw new InvalidResourceException( new Infraction( 0x2001, "rdf.type", AppRoleDescription.Resource.CLASS.getURI().stringValue() ) );

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
			else throw new InvalidRDFTypeException( new Infraction( 0x2001, "rdf.type", AgentDescription.Resource.CLASS.getURI().stringValue() ) );
		}
		return false;
	}

	private boolean isPlatformAgent( URI agent ) {
		return platformAgentRepository.exists( agent );
	}

	private void createAgentsContainer( AppRole appRole ) {
		URI agentsContainerURI = appRoleRepository.getAgentsContainerURI( appRole.getURI() );
		RDFResource resource = new RDFResource( agentsContainerURI );
		DirectContainer container = DirectContainerFactory.getInstance().create( resource, appRole.getURI(), AppRoleDescription.Property.AGENT.getURI() );
		sourceRepository.createAccessPoint( appRole.getURI(), container );
		aclRepository.createACL( container.getURI() );
	}

	private void validateHasParent( URI childURI ) {
		if ( ! sourceRepository.exists( childURI ) ) throw new ResourceDoesntExistException();
		Set<URI> parentsRoles = appRoleRepository.getParentsURI( childURI );
		if ( ! parentsRoles.isEmpty() ) throw new AlreadyHasAParentException();
	}
}
