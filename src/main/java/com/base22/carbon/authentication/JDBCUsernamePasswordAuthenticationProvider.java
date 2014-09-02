package com.base22.carbon.authentication;

import java.security.NoSuchAlgorithmException;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.base22.carbon.CarbonException;
import com.base22.carbon.agents.Agent;

public class JDBCUsernamePasswordAuthenticationProvider extends JDBCCarbonAuthenticationProvider implements AuthenticationProvider {

	public void init() {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace("Username/Password Authentication enabled through JDBC");
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

		Agent agent = null;
		try {
			agent = agentLoginDetailsDAO.findByEmail(username);
		} catch (CarbonException e) {
			throw new AuthenticationServiceException("The agentLoginDetails couldn't be retrieved.");
		}

		// Check if the agentDetails was found
		if ( agent != null ) {
			// It was
			// Check that the password matches
			String salt = agent.getSalt();
			String saltedPassword = AuthenticationUtil.saltPassword(password, salt);
			String hashedPassword = null;

			try {
				hashedPassword = AuthenticationUtil.hashPassword(saltedPassword);
			} catch (NoSuchAlgorithmException exception) {
				throw new AuthenticationServiceException("Password validation failed");
			}

			if ( agent.getPassword().equals(hashedPassword) ) {
				return createAgentAuthenticationToken(agent);
			}

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
