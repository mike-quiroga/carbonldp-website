package com.carbonldp.authentication;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.carbonldp.agents.AgentService;

public class SesameUsernamePasswordAuthenticationProvider extends AbstractAuthenticationProvider {

	private final AgentService agentService;

	public SesameUsernamePasswordAuthenticationProvider(AgentService agentService) {
		this.agentService = agentService;
	}

	public void init() {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace("Username/Password Authentication enabled through Sesame");
		}
	}

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

		agentService.findByEmail("some-email");

		// TODO: Find agent based on the username (email)
		// TODO: Hash password and compare it with the one stored
		// TODO: Create the AuthenticationToken
		// TODO: Throw BadCredentialsException if not found/incorrect password

		return null;
	}

	public boolean supports(Class<?> authentication) {
		if ( UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication) ) {
			return true;
		}
		return false;
	}
}
