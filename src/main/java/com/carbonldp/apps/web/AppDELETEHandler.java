package com.carbonldp.apps.web;

import com.carbonldp.apps.AppService;
import com.carbonldp.ldp.web.AbstractDELETERequestHandler;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

@RequestHandler
public class AppDELETEHandler extends AbstractDELETERequestHandler {

	AppService appService;

	@Autowired
	public AppDELETEHandler( AppService appService ) {
		this.appService = appService;
	}

	@Override
	protected void delete( URI targetURI ) {

		appService.delete( targetURI );
	}

}
