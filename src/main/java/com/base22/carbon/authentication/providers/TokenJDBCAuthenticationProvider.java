package com.base22.carbon.authentication.providers;

import java.util.UUID;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.token.Token;
import org.springframework.security.core.token.TokenService;

import com.base22.carbon.CarbonException;
import com.base22.carbon.agents.Agent;
import com.base22.carbon.authentication.AgentTokenAuthenticationToken;
import com.base22.carbon.authentication.AuthenticationUtil;

public class TokenJDBCAuthenticationProvider extends CarbonJDBCAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	private TokenService tokenService;

	public void init() {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace("Token Authentication enabled through JDBC");
		}
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if ( ! (authentication instanceof AgentTokenAuthenticationToken) ) {
			return null;
		}

		String agentTokenKey = (String) authentication.getCredentials();

		Token agentToken = tokenService.verifyToken(agentTokenKey);

		if ( ! tokenIsStillAlive(agentToken) ) {
			throw new AuthenticationServiceException("The token has expired.");
		}

		if ( ! tokenContainsUUID(agentToken) ) {
			throw new AuthenticationServiceException("The token isn't valid.");
		}

		UUID agentUUID = getTokenUUID(agentToken);

		Agent agent = null;
		try {
			agent = agentLoginDetailsDAO.findByUUID(agentUUID);
		} catch (CarbonException e) {
			throw new AuthenticationServiceException("The agentLoginDetails couldn't be retrieved.");
		}

		if ( agent != null ) {
			return createAgentAuthenticationToken(agent);
		}

		throw new BadCredentialsException("The token isn't valid.");
	}

	private boolean tokenIsStillAlive(Token agentToken) {
		long tokenLife = 1000 * 60 * configurationService.getTokenCookieLifeInMinutes();
		long now = DateTime.now().getMillis();
		long tokenCreationTime = agentToken.getKeyCreationTime();
		if ( (tokenCreationTime + tokenLife) > now ) {
			return true;
		}
		return false;
	}

	private boolean tokenContainsUUID(Token agentToken) {
		return AuthenticationUtil.isUUIDString(agentToken.getExtendedInformation());
	}

	private UUID getTokenUUID(Token agentToken) {
		return AuthenticationUtil.restoreUUID(agentToken.getExtendedInformation());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		if ( AgentTokenAuthenticationToken.class.isAssignableFrom(authentication) ) {
			return true;
		}
		return false;
	}

	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}

}
