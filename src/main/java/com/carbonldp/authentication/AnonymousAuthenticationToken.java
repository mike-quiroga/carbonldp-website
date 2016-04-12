package com.carbonldp.authentication;

import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.roles.AppRolesHolder;
import com.carbonldp.authorization.Platform;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class AnonymousAuthenticationToken extends AbstractAuthenticationToken implements AppRolesHolder {

	private final Set<AppRole> appRoles;
	private static final String NAME = "ANONYMOUS";

	public AnonymousAuthenticationToken( Collection<Platform.Role> platformRoles, Collection<Platform.Privilege> platformPrivileges ) {
		super( platformRoles, platformPrivileges );

		super.setAuthenticated( true );
		this.appRoles = new HashSet<>();
	}

	@Override
	public Set<AppRole> getAppRoles() {
		return Collections.unmodifiableSet( appRoles );
	}

	@Override
	public void setAppRoles( Set<AppRole> appRoles ) {
		this.appRoles.clear();
		this.appRoles.addAll( appRoles );
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

	@Override
	public String getName() {
		return AnonymousAuthenticationToken.NAME;
	}

	@Override
	public void setAuthenticated( boolean authenticated ) throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}
}
