package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRole;
import org.openrdf.model.URI;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface AppRolesHolder {

	public Set<AppRole> getAppRoles();

	public void setAppRoles( Set<AppRole> appsRoles );
}
