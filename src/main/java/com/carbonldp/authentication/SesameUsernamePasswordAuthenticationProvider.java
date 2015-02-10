package com.carbonldp.authentication;

import java.security.NoSuchAlgorithmException;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentService;
import com.carbonldp.authorization.PlatformRoleService;
import com.carbonldp.authorization.RunWith;
import com.carbonldp.commons.utils.AuthenticationUtil;

public class SesameUsernamePasswordAuthenticationProvider extends AbstractAuthenticationProvider {

	private final AgentService agentService;
	private final PlatformRoleService platformRoleService;

	public SesameUsernamePasswordAuthenticationProvider(AgentService agentService, PlatformRoleService platformRoleService) {
		this.agentService = agentService;
		this.platformRoleService = platformRoleService;
	}

	public void init() {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace("Username/Password Authentication enabled through Sesame");
		}
	}

	@RunWith(roles = { "ROLE_SYSTEM" })
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Object rawPrincipal = authentication.getPrincipal();
		Object rawCredentials = authentication.getCredentials();

		if ( ! (rawPrincipal.getClass().equals(String.class) && rawCredentials.getClass().equals(String.class)) ) {
			throw new BadCredentialsException("Wrong credentials");
		}

		String username = (String) rawPrincipal;
		String password = (String) rawCredentials;

		if ( username.trim().length() == 0 || password.trim().length() == 0 ) {
			throw new BadCredentialsException("Wrong credentials");
		}

		Agent agent = agentService.findByEmail(username);

		if ( agent == null ) throw new BadCredentialsException("Wrong credentials");

		String salt = agent.getSalt();
		String saltedPassword = AuthenticationUtil.saltPassword(password, salt);
		String hashedPassword = null;

		try {
			hashedPassword = AuthenticationUtil.hashPassword(saltedPassword);
		} catch (NoSuchAlgorithmException exception) {
			throw new AuthenticationServiceException("Password validation failed");
		}

		if ( agent.getPassword().equals(hashedPassword) ) {
			platformRoleService.getPlatformRolesOfAgent(agent);
			// TODO: Create authentication token
		}

		throw new BadCredentialsException("Wrong credentials");
	}

	public boolean supports(Class<?> authentication) {
		if ( UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication) ) {
			return true;
		}
		return false;
	}
}
