package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.exceptions.ResourceDoesntExistException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.openrdf.model.URI;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Set;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class SesameAppRoleService extends AbstractSesameLDPService implements AppRoleService {
	protected ContainerService containerService;

	public SesameAppRoleService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, ContainerService containerService ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		this.containerService = containerService;
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
		validate();
		containerService.addMember( containerURI, member );

	}

	private void validate() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new BadCredentialsException( "invalid authentication token" );
		AgentAuthenticationToken agentAuthenticationToken = (AgentAuthenticationToken) authentication;
		Set<AppRole> agentAppRoles = agentAuthenticationToken.getAppRoles( AppContextHolder.getContext().getApplication().getURI() );
		Set<URI> 
	}

}
