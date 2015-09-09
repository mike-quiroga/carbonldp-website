package com.carbonldp.apps.web;

import com.carbonldp.ldp.sources.AbstractPATCHRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;

import java.util.Set;

@RequestHandler
public class AppPATCHHandler extends AbstractPATCHRequestHandler {
	@Override
	protected void deleteResourceViews( URI sourceURI, Set resourceViews ) {
		super.deleteResourceViews( sourceURI, resourceViews );
	}

	@Override
	protected void setResourceViews( URI sourceURI, Set resourceViews ) {
		super.setResourceViews( sourceURI, resourceViews );
	}

	@Override
	protected void addResourceViews( URI sourceURI, Set resourceViews ) {
		super.addResourceViews( sourceURI, resourceViews );
	}
}
