package com.carbonldp.apps.roles.web;

import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.ldp.web.AbstractDELETERequestHandler;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@RequestHandler
public class AppRolesDELETEHandler extends AbstractDELETERequestHandler {
	private final AppRoleService appRoleService;

	@Autowired
	public AppRolesDELETEHandler( AppRoleService appRoleService ) {
		this.appRoleService = appRoleService;
	}

	@Override
	protected void delete( URI targetURI ) {
		appRoleService.delete( targetURI );
	}

}
