package com.carbonldp.authentication;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.apps.context.RunInPlatformContext;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.authorization.RunWith;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;

public class PlatformAgentUsernamePasswordAuthenticationProvider extends SesameUsernamePasswordAuthenticationProvider {

	public PlatformAgentUsernamePasswordAuthenticationProvider( AgentRepository agentRepository, PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		super( agentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	@PostConstruct
	public void init() {
		if ( LOG.isTraceEnabled() ) LOG.trace( "Platform Agent Username/Password Authentication enabled through Sesame" );
	}

	@Transactional
	@RunWith( platformRoles = {Platform.Role.SYSTEM} )
	@RunInPlatformContext
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		String username = getUsername( authentication );
		String password = getPassword( authentication );

		validateCredentials( username, password );

		Agent agent = agentRepository.findByEmail( username );

		String hashedPassword = getHashedPassword( password, agent );

		if ( ! passwordsMatch( hashedPassword, agent ) ) throw new BadCredentialsException( "Wrong credentials" );

		return createAgentAuthenticationToken( agent );
	}

	public boolean supports( Class<?> authentication ) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom( authentication );
	}

}
