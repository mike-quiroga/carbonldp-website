package com.carbonldp.authentication.token;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.apps.context.RunInPlatformContext;
import com.carbonldp.authentication.AbstractSesameAuthenticationProvider;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.authorization.RunWith;
import org.openrdf.model.URI;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author NestorVenegas
 * @since 0.15.0_ALPHA
 */
public class JWTAuthenticationProvider extends AbstractSesameAuthenticationProvider {
	public JWTAuthenticationProvider( AgentRepository agentRepository, PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		super( agentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	@Transactional
	@RunWith( platformRoles = {Platform.Role.SYSTEM} )
	@RunInPlatformContext
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		URI agentURI = getAgentURI( authentication );
		validateCredentials( agentURI );

		Agent agent = agentRepository.get( agentURI );
		if ( agent == null || agent.getBaseModel().size() == 0 ) throw new BadCredentialsException( "Wrong credentials" );

		return createAgentAuthenticationToken( agent );

	}

	@Override
	public boolean supports( Class<?> authentication ) {
		return JWTAuthenticationToken.class.isAssignableFrom( authentication );
	}

	protected URI getAgentURI( Authentication authentication ) {
		if ( ! ( authentication instanceof JWTAuthenticationToken ) ) throw new IllegalArgumentException( "Authentication is not instance of JWTAuthentication token" );
		JWTAuthenticationToken authenticationToken = (JWTAuthenticationToken) authentication;

		return authenticationToken.getAgentURI();
	}

	protected void validateCredentials( URI agentURI ) {
		if ( agentURI.stringValue().trim().length() == 0 ) throw new BadCredentialsException( "Wrong credentials" );
	}
}
