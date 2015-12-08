package com.carbonldp.apps.roles;

import com.carbonldp.agents.AgentDescription;
import com.carbonldp.agents.platform.PlatformAgentRepository;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.exceptions.AuthorizationException;
import com.carbonldp.exceptions.InvalidRDFTypeException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerDescription;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.spring.TransactionWrapper;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since _version_
 */

public class SesameAppRoleService extends AbstractSesameLDPService implements AppRoleService {
	protected final ContainerService containerService;
	protected final AppRoleRepository appRoleRepository;
	protected final PlatformAgentRepository platformAgentRepository;

	public SesameAppRoleService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, ContainerService containerService, AppRoleRepository appRoleRepository, PlatformAgentRepository platformAgentRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		this.appRoleRepository = appRoleRepository;
		this.containerService = containerService;
		this.platformAgentRepository = platformAgentRepository;
	}

	@Override
	public void addAgentMembers( URI appRoleAgentContainerURI, Set<URI> agents ) {
		for ( URI agent : agents ) {
			addAgentMember( appRoleAgentContainerURI, agent );
		}
	}

	@Override
	public void addAgentMember( URI appRoleAgentContainerURI, URI agent ) {
		if ( ( ! sourceRepository.exists( appRoleAgentContainerURI ) ) ) throw new ResourceDoesntExistException();
		if ( ! isAppAgent( agent ) && ! isPlatformAgent( agent ) ) throw new InvalidRDFTypeException( new Infraction( 0x2001, "rdf.type", AgentDescription.Resource.CLASS.getURI().stringValue() ) );
		validatePermissionToModifyAgent( appRoleAgentContainerURI );

		containerService.addMember( appRoleAgentContainerURI, agent );

		DateTime modifiedTime = DateTime.now();
		URI membershipResource = containerRepository.getTypedRepository( containerService.getContainerType( appRoleAgentContainerURI ) ).getMembershipResource( appRoleAgentContainerURI );
		sourceRepository.touch( membershipResource, modifiedTime );
	}

	@Override
	public void removeAgentMembers( URI appRoleAgentContainerURI, Set<URI> agents ) {
		for ( URI agent : agents ) {
			removeAgentMember( appRoleAgentContainerURI, agent );
		}
	}

	@Override
	public void removeAgentMember( URI appRoleAgentContainerURI, URI agent ) {
		if ( ( ! sourceRepository.exists( appRoleAgentContainerURI ) ) ) throw new ResourceDoesntExistException();
		if ( ! isAppAgent( agent ) && ! isPlatformAgent( agent ) ) throw new InvalidRDFTypeException( new Infraction( 0x2001, "rdf.type", AgentDescription.Resource.CLASS.getURI().stringValue() ) );

		validatePermissionToModifyAgent( appRoleAgentContainerURI );

		containerService.removeMember( appRoleAgentContainerURI, agent );

		URI membershipResource = containerRepository.getTypedRepository( containerService.getContainerType( appRoleAgentContainerURI ) ).getMembershipResource( appRoleAgentContainerURI );
		sourceRepository.touch( membershipResource );
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

	private void validatePermissionToModifyAgent( URI appRoleAgentContainerURI ) {
		ContainerDescription.Type containerType = containerRepository.getContainerType( appRoleAgentContainerURI );
		URI role = containerRepository.getTypedRepository( containerType ).getMembershipResource( appRoleAgentContainerURI );
		if ( ! isMemberOfRoleHierarchy( role ) ) {
			Map<String, String> parametersException = new LinkedHashMap<>();
			parametersException.put( "action", "add agent" );
			parametersException.put( "uri", role.stringValue() );
			throw new AuthorizationException( new Infraction( 0x7001, parametersException ) );
		}
	}

	private boolean isMemberOfRoleHierarchy( URI role ) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new BadCredentialsException( "invalid authentication token" );
		AgentAuthenticationToken agentAuthenticationToken = (AgentAuthenticationToken) authentication;
		Set<AppRole> agentAppRoles = agentAuthenticationToken.getAppRoles( AppContextHolder.getContext().getApplication().getURI() );
		Set<URI> parentsRoles = appRoleRepository.getParentsURI( role );
		for ( AppRole appRole : agentAppRoles ) {
			if ( parentsRoles.contains( appRole.getSubject() ) ) {
				return true;
			}
		}
		return false;
	}
}
