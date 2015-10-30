package com.carbonldp.authentication;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.apps.context.RunInAppContext;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.authorization.RunWith;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

public class AppsAgentUsernamePasswordAuthenticationProvider extends SesameUsernamePasswordAuthenticationProvider {
	public AppsAgentUsernamePasswordAuthenticationProvider( AgentRepository agentRepository, PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		super( agentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	@Transactional
	@RunWith( platformRoles = {Platform.Role.SYSTEM} )
	@RunInAppContext
	@Override
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		if ( AppContextHolder.getContext().isEmpty() ) return null;
		String username = getUsername( authentication );
		String password = getPassword( authentication );

		validateCredentials( username, password );

		Agent agent = agentRepository.findByEmail( username );

		if ( agent == null ) throw new BadCredentialsException( "Wrong credentials" );

		String hashedPassword = getHashedPassword( password, agent );
		if ( ! passwordsMatch( hashedPassword, agent ) ) throw new BadCredentialsException( "Wrong credentials" );

		return createAgentAuthenticationToken( agent );
	}

	@Override
	public boolean supports( Class<?> authentication ) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom( authentication );
	}
}
