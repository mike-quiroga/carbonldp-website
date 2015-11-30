package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRole;
import org.openrdf.model.URI;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public interface AppRoleService {
	public boolean exists( URI appRoleURI );

	public void create( AppRole appRole );
}
