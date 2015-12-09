package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.exceptions.AuthorizationException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.Infraction;
import com.carbonldp.spring.TransactionWrapper;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.URI;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class SesameAppRoleService extends AbstractSesameLDPService implements AppRoleService {

	private final AppRoleRepository appRoleRepository;

	public SesameAppRoleService( TransactionWrapper transactionWrapper, RDFSourceRepository sourceRepository, ContainerRepository containerRepository, ACLRepository aclRepository, AppRoleRepository appRoleRepository ) {
		super( transactionWrapper, sourceRepository, containerRepository, aclRepository );
		this.appRoleRepository = appRoleRepository;
	}

	@Override
	public boolean exists( URI appRoleURI ) {
		return appRoleRepository.exists( appRoleURI );
	}

	@Override
	public void delete( URI appRoleURI ) {
		if ( ! exists( appRoleURI ) ) throw new NotFoundException();
		validateModifyRole( appRoleURI );
		appRoleRepository.delete( appRoleURI );
	}

	private void validateModifyRole( URI appRoleURI ) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new BadCredentialsException( "invalid authentication token" );
		AgentAuthenticationToken agentAuthenticationToken = (AgentAuthenticationToken) authentication;
		Set<AppRole> agentAppRoles = agentAuthenticationToken.getAppRoles( AppContextHolder.getContext().getApplication().getURI() );

		Set<URI> parentsRoles = appRoleRepository.getParentsURI( appRoleURI );
		boolean isParent = false;
		for ( AppRole appRole : agentAppRoles ) {
			if ( parentsRoles.contains( appRole.getSubject() ) ) {
				isParent = true;
				break;
			}
		}
		if ( ! isParent ) {
			Map<String, String> parametersException = new LinkedHashMap<>();
			parametersException.put( "action", "delete role" );
			parametersException.put( "uri", appRoleURI.stringValue() );
			throw new AuthorizationException( new Infraction( 0x7001, parametersException ) );
		}
	}
}
