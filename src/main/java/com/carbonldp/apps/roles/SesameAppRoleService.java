package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.AppRoleFactory;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.exceptions.AlreadyHasAParentException;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.ResourceAlreadyExistsException;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.spring.TransactionWrapper;
import org.openrdf.model.URI;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Set;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since _version_
 */
public class SesameAppRoleService extends AbstractSesameLDPService implements AppRoleService {
	private final ContainerService containerService;
	private final AppRoleRepository appRoleRepository;

	public SesameAppRoleService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, ContainerService containerService, AppRoleRepository appRoleRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		this.appRoleRepository = appRoleRepository;
		this.containerService = containerService;
	}

	@Override
	public boolean exists( URI appRoleURI ) {

	}

	@Override
	public void create( AppRole appRole ) {
		if ( exists( appRole.getURI() ) ) throw new ResourceAlreadyExistsException();
		validate( appRole );

	}

	@Override
	public void addChildMembers( URI parentRole, Set<URI> childss ) {
		for ( URI member : childss ) {
			addChildMember( parentRole, member );
		}
	}

	@Override
	public void addChildMember( URI parentRole, URI child ) {
		if ( ! sourceRepository.exists( parentRole ) ) throw new ResourceDoesntExistException();
		validateAddChild( parentRole );
		validateHasParent( child );
		containerService.addMember( parentRole, child );

	}

	private void validateHasParent( URI childURI ) {
		Set<URI> parentsRoles = appRoleRepository.getParentsURI( childURI );
		if ( ! parentsRoles.isEmpty() ) throw new AlreadyHasAParentException();
	}

	private void validate( AppRole appRole ) {
		List<Infraction> infractions = AppRoleFactory.getInstance().validate( appRole );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private void validateAddChild( URI parentRole ) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new BadCredentialsException( "invalid authentication token" );
		AgentAuthenticationToken agentAuthenticationToken = (AgentAuthenticationToken) authentication;
		Set<AppRole> agentAppRoles = agentAuthenticationToken.getAppRoles( AppContextHolder.getContext().getApplication().getURI() );
		Set<URI> parentsRoles = appRoleRepository.getParentsURI( parentRole );
		boolean isParent = false;
		for ( AppRole appRole : agentAppRoles ) {
			if ( parentsRoles.contains( appRole.getSubject() ) ) {
				isParent = true;
				break;
			}
		}
		if ( isParent ) throw new BadCredentialsException( "Unauthorized" );
	}
}


