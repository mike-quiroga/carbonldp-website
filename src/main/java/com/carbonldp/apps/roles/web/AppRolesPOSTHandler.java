package com.carbonldp.apps.roles.web;

import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.AppRoleFactory;
import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author JorgeEspinosa
 * @since 0.18.0-ALPHA
 */
@RequestHandler
public class AppRolesPOSTHandler extends AbstractRDFPostRequestHandler<AppRole> {

	private AppRoleService appRoleService;

	@Override
	protected AppRole getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return AppRoleFactory.getInstance().create( requestBasicContainer );
	}

	@Override
	protected void createChild( IRI targetIRI, AppRole documentResourceView ) {
		appRoleService.create( documentResourceView );
	}

	@Autowired
	public void setAppRolesPOSTHandler( AppRoleService appRoleService ) {
		this.appRoleService = appRoleService;
	}
}
