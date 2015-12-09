package com.carbonldp.apps.roles;

import org.openrdf.model.URI;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public interface AppRoleService {
	public boolean exists( URI appURI );

	public void delete( URI appURI );
}
