package com.base22.carbon.authentication.providers;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.base22.carbon.agents.Agent;
import com.base22.carbon.authentication.KeyAuthenticationToken;

public class KeyJDBCAuthenticationProvider extends CarbonJDBCAuthenticationProvider implements AuthenticationProvider {

	public void init() {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace("Key Authentication enabled through JDBC");
		}
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Object rawCredentials = authentication.getCredentials();

		if ( ! rawCredentials.getClass().equals(String.class) ) {
			return null;
		}

		String key = (String) rawCredentials;

		if ( key.trim().length() == 0 ) {
			throw new BadCredentialsException("Wrong credentials");
		}

		Agent agentDetails = null;
		try {
			agentDetails = agentLoginDetailsDAO.findByKey(key);
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		// Check if the agentDetails was found
		if ( agentDetails != null ) {
			// It was
			// Check that the key matches
			if ( agentDetails.getKey().equals(key) ) {
				return createAgentAuthenticationToken(agentDetails);
			}
		}

		throw new BadCredentialsException("Wrong credentials");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		if ( KeyAuthenticationToken.class.isAssignableFrom(authentication) ) {
			return true;
		}
		return false;
	}
}
