package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.AppRoleFactory;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.exceptions.*;
import com.carbonldp.ldp.AbstractSesameLDPService;
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
import java.util.List;
import java.util.Map;
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
		return appRoleRepository.exists( appRoleURI );
	}

	@Override
	public void create( AppRole appRole ) {
		if ( exists( appRole.getURI() ) ) throw new ResourceAlreadyExistsException();
		validate( appRole );

		appRoleRepository.create( appRole );
	}

	@Override
	public void addChildMembers( URI parentRole, Set<URI> childs ) {
		for ( URI member : childs ) {
			addChildMember( parentRole, member );
		}
	}

	@Override
	public void addChildMember( URI parentRoleURI, URI child ) {
		if ( ! sourceRepository.exists( parentRoleURI ) ) throw new ResourceDoesntExistException();
		validateAddChild( parentRoleURI );
		validateHasParent( child );
		containerService.addMember( parentRoleURI, child );

		DateTime modifiedTime = DateTime.now();
		sourceRepository.touch( parentRoleURI, modifiedTime );

	}

	private void validateHasParent( URI childURI ) {
		if ( ! sourceRepository.exists( childURI ) ) throw new ResourceDoesntExistException();
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
		parentsRoles.add( parentRole );
		boolean isParent = false;
		for ( AppRole appRole : agentAppRoles ) {
			if ( parentsRoles.contains( appRole.getSubject() ) ) {
				isParent = true;
				break;
			}
		}

		if ( ! isParent ) {
			Map<String, String> parametersException = new LinkedHashMap<>();
			parametersException.put( "action", "add child" );
			parametersException.put( "uri", parentRole.stringValue() );
			throw new AuthorizationException( new Infraction( 0x7001, parametersException ) );
		}
	}
}


