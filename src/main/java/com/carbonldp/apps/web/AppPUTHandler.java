package com.carbonldp.apps.web;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppFactory;
import com.carbonldp.apps.AppService;
import com.carbonldp.ldp.web.AbstractPUTRequestHandler;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.BadRequestException;
import org.openrdf.model.URI;
import org.springframework.beans.factory.annotation.Autowired;

@RequestHandler
public class AppPUTHandler extends AbstractPUTRequestHandler<App> {

	private AppService appService;

	@Override
	protected void validateDocumentResource( URI targetURI, RDFResource requestDocumentResource ) {
		super.validateDocumentResource( targetURI, requestDocumentResource );
		if ( ! AppFactory.is( requestDocumentResource ) ) throw new BadRequestException( "The document resource sent is not a cs:App." );
	}

	@Override
	protected App getDocumentResourceView( RDFResource requestDocumentResource ) {
		return new App( requestDocumentResource );
	}

	@Override
	protected void replaceResource( URI targetURI, App documentResourceView ) {
		appService.replace( documentResourceView );
	}

	@Override
	protected void validateDocumentResourceView( App documentResourceView ) {}

	@Autowired
	public void setAppService( AppService appService ) {
		this.appService = appService;
	}
}