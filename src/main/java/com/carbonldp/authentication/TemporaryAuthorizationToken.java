package com.carbonldp.authentication;

import com.carbonldp.authorization.Platform;
import org.springframework.security.core.Authentication;

import java.util.Collection;

public class TemporaryAuthorizationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = -717042178885895374L;

	private final Authentication originalAuthenticationObject;

	public TemporaryAuthorizationToken(Authentication originalAuthenticationObject, Collection<Platform.Role> platformRoles,
			Collection<Platform.Privilege> platformPrivileges) {
		super( platformRoles, platformPrivileges );

		this.originalAuthenticationObject = originalAuthenticationObject;

		setAuthenticated( true );
	}

	public Authentication getOriginalAuthenticationObject() {
		return originalAuthenticationObject;
	}

	@Override
	public String getName() {
		return "[TemporaryAuthorization]";
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}
}
