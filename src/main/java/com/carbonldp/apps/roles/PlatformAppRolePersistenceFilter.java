package com.carbonldp.apps.roles;

import com.carbonldp.apps.context.AppContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class PlatformAppRolePersistenceFilter extends AppRolePersistenceFilter {
	public PlatformAppRolePersistenceFilter( AppRoleRepository appRoleRepository ) {
		super( appRoleRepository );
	}

	@Override
	protected void applyFilter( HttpServletRequest request, HttpServletResponse response ) {
		super.applyFilter( request, response );
		AppContextHolder.clearContext();
	}
}
