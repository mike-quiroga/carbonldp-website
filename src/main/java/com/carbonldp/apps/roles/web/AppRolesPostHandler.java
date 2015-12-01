package com.carbonldp.apps.roles.web;

import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.AppRoleFactory;
import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@RequestHandler
public class AppRolesPostHandler extends AbstractRDFPostRequestHandler<AppRole> {

	private final AppRoleService appRoleService;

	@Override
	protected AppRole getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return AppRoleFactory.getInstance().create( requestBasicContainer);
	}

	@Override
	protected void createChild( URI targetURI, AppRole documentResourceView ) {
		appRoleService.create( documentResourceView );
	}

	@Autowired
	public AppRolesPOSTHandler( AppRoleService appRoleService ) {
		this.appRoleService = appRoleService;
	}
}
