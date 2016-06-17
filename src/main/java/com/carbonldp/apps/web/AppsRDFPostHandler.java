package com.carbonldp.apps.web;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppFactory;
import com.carbonldp.apps.AppService;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.web.AbstractRDFPostRequestHandler;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

@RequestHandler
public class AppsRDFPostHandler extends AbstractRDFPostRequestHandler<App> {

	private final AppService appService;

	@Autowired
	public AppsRDFPostHandler( AppService appService ) {
		this.appService = appService;
	}

	@Override
	protected App getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return AppFactory.getInstance().create( requestBasicContainer );
	}

	@Override
	protected void createChild( IRI targetIRI, App documentResourceView ) {
		appService.create( documentResourceView );
	}
}
