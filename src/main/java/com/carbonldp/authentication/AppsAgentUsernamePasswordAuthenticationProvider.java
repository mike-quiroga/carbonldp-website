package com.carbonldp.authentication;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class AppsAgentUsernamePasswordAuthenticationProvider extends SesameUsernamePasswordAuthenticationProvider {
	public AppsAgentUsernamePasswordAuthenticationProvider( AgentRepository agentRepository, PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		super( agentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	@Override
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		if ( AppContextHolder.getContext().isEmpty() ) return null;

		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}

	@Override
	public boolean supports( Class<?> authentication ) {
		// TODO: Implement
		throw new RuntimeException( "Not Implemented" );
	}
}
