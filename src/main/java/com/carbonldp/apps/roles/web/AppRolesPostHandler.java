package com.carbonldp.apps.roles.web;

import com.carbonldp.apps.AppRole;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import org.openrdf.model.URI;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class AppRolesPostHandler extends AbstractRDFPostRequestHandler<AppRole> {
	@Override
	protected AppRole getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return null;
	}

	@Override
	protected void createChild( URI targetURI, AppRole documentResourceView ) {

	}
}
