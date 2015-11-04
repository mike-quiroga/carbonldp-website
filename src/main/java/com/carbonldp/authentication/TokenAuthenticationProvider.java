package com.carbonldp.authentication;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class TokenAuthenticationProvider extends AbstractSesameAuthenticationProvider {
	public TokenAuthenticationProvider( AgentRepository agentRepository, PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		super( agentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	@Override
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		throw new NotImplementedException();
	}

	@Override
	public boolean supports( Class<?> aClass ) {
		throw new NotImplementedException();
	}
}
