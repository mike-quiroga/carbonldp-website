package com.carbonldp.authentication.token;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.apps.context.RunInPlatformContext;
import com.carbonldp.authentication.AbstractSesameAuthenticationProvider;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.authorization.RunWith;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class TokenAuthenticationProvider extends AbstractSesameAuthenticationProvider {
	public TokenAuthenticationProvider( AgentRepository agentRepository, PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		super( agentRepository, platformRoleRepository, platformPrivilegeRepository );
	}

	@Transactional
	@RunWith( platformRoles = {Platform.Role.SYSTEM} )
	@RunInPlatformContext
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		throw new NotImplementedException();
	}

	@Override
	public boolean supports( Class<?> aClass ) {

		throw new NotImplementedException();
	}
}
