package com.carbonldp.apps.roles.web;

import com.carbonldp.apps.AppService;
import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.ldp.web.AbstractDELETERequestHandler;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author arima
 * @since _version_
 */
public class AppRolesDELETEAgentsHandler extends AbstractDELETERequestHandler {

	AppRoleService appRoleService;

	@Autowired
	public AppRolesDELETEAgentsHandler( AppService appService ) {
		this.appRoleService = appRoleService;
	}

	@Override
	protected void delete( URI targetURI ) {
		appRoleService.delete( targetURI );
	}

}
