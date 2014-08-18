package com.base22.carbon.security.tokens;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class AgentTokenAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = - 4411908114707433445L;

	private String credentials;

	public AgentTokenAuthenticationToken(String credentials) {
		super(null);
		this.credentials = credentials;
		setAuthenticated(false);
	}

	public String getCredentials() {
		return this.credentials;
	}

	public Object getPrincipal() {
		return null;
	}

	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		if ( isAuthenticated ) {
			throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}

		super.setAuthenticated(false);
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		credentials = null;
	}
}
