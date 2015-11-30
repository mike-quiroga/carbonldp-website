package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.AppRoleFactory;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authorization.acl.ACLRepository;
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
import org.springframework.util.Assert;

import java.util.List;
import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class SesameAppRoleService extends AbstractSesameLDPService implements AppRoleService {
	private final AppRepository appRepository;
	private final ContainerService containerService;

	public SesameAppRoleService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, AppRepository appRepository, ContainerService containerService ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		Assert.notNull( appRepository );
		this.appRepository = appRepository;
		this.containerService = containerService;
	}

	@Override
	public boolean exists( URI appRoleURI ) {
		return appRepository.exists( appRoleURI );
	}

	@Override
	public void create( AppRole appRole ) {
		if ( exists( appRole.getURI() ) ) throw new ResourceAlreadyExistsException();
		validate( appRole );

	}

	@Override
	public void addChildMembers( URI containerURI, Set<URI> members ) {
		for ( URI member : members ) {
			addChildMember( containerURI, member );
		}
	}

	@Override
	public void addChildMember( URI containerURI, URI member ) {
		if ( ! sourceRepository.exists( containerURI ) ) throw new ResourceDoesntExistException();
		hasPermissions();
		containerService.addMember( containerURI, member );

	}

	private void validate( AppRole appRole ) {
		List<Infraction> infractions = AppRoleFactory.getInstance().validate( appRole );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private void hasPermissions() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new BadCredentialsException( "invalid authentication token" );
		AgentAuthenticationToken agentAuthenticationToken = (AgentAuthenticationToken) authentication;
		Set<AppRole> agentAppRoles = agentAuthenticationToken.getAppRoles( AppContextHolder.getContext().getApplication().getURI() );
	}
}


