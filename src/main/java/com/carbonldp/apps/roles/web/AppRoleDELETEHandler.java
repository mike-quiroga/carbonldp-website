package com.carbonldp.apps.roles.web;

import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.ldp.web.AbstractDELETERequestHandler;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author JorgeEspinosa
 * @since 0.20.0-ALPHA
 */
@RequestHandler
public class AppRoleDELETEHandler extends AbstractDELETERequestHandler {
	private final AppRoleService appRoleService;

	@Autowired
	public AppRoleDELETEHandler( AppRoleService appRoleService ) {
		this.appRoleService = appRoleService;
	}

	@Override
	protected void delete( IRI targetIRI ) {
		appRoleService.delete( targetIRI );
	}

}
