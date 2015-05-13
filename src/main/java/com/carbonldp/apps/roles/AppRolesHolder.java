package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRole;
import org.openrdf.model.URI;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface AppRolesHolder {
	public Map<URI, Set<AppRole>> getAppsRoles();

	public Set<AppRole> getAppRoles( URI appURI );

	public void setAppRoles( Map<URI, Set<AppRole>> appsRoles );

	public void setAppRoles( URI appURI, Collection<AppRole> appRoles );
}
