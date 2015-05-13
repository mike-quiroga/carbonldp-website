package com.carbonldp.authentication;

import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.roles.AppRolesHolder;
import com.carbonldp.authorization.Platform;
import org.openrdf.model.URI;

import java.util.*;
import java.util.stream.Collectors;

public final class AnonymousAuthenticationToken extends AbstractAuthenticationToken implements AppRolesHolder {

	private final Map<URI, Set<AppRole>> appsRoles;
	private static final String NAME = "ANONYMOUS";

	public AnonymousAuthenticationToken( Collection<Platform.Role> platformRoles, Collection<Platform.Privilege> platformPrivileges ) {
		super( platformRoles, platformPrivileges );

		super.setAuthenticated( true );
		this.appsRoles = new HashMap<>();
	}

	@Override
	public Map<URI, Set<AppRole>> getAppsRoles() {
		return Collections.unmodifiableMap( appsRoles );
	}

	@Override
	public Set<AppRole> getAppRoles( URI appURI ) {
		if ( ! appsRoles.containsKey( appURI ) ) return new HashSet<>();
		return appsRoles.get( appURI );
	}

	@Override
	public void setAppRoles( Map<URI, Set<AppRole>> appsRoles ) {
		for ( URI appURI : appsRoles.keySet() ) {
			Set<AppRole> appRoles = appsRoles.get( appURI );
			setAppRoles( appURI, appRoles );
		}
	}

	@Override
	public void setAppRoles( URI appURI, Collection<AppRole> appRoles ) {
		Set<AppRole> tempAppRoles = appRoles.stream()
											.filter( appRole -> appRole != null )
											.collect( Collectors.toSet() );
		appsRoles.put( appURI, Collections.unmodifiableSet( tempAppRoles ) );
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
