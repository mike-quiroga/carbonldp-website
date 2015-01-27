package com.carbonldp.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class TemporaryAuthorizationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = - 717042178885895374L;

	private final Authentication originalAuthenticationObject;

	public TemporaryAuthorizationToken(Collection<? extends GrantedAuthority> authorities, Authentication originalAuthenticationObject) {
		super(authorities);
		this.originalAuthenticationObject = originalAuthenticationObject;
		setAuthenticated(true);
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	public Authentication getOriginalAuthenticationObject() {
		return originalAuthenticationObject;
	}
}
