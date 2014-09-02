package com.base22.carbon.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class KeyAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 7766182232264524097L;

	private String credentials;

	public KeyAuthenticationToken(String credentials) {
		super(null);
		this.credentials = credentials;
		setAuthenticated(false);
	}

	public String getCredentials() {
		return this.credentials;
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

	@Override
	public Object getPrincipal() {
		return null;
	}
}
