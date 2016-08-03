package com.carbonldp.apps.web;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppFactory;
import com.carbonldp.apps.AppService;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.sources.AbstractPUTRequestHandler;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.beans.factory.annotation.Autowired;

@RequestHandler
public class AppRDFPutToRDFSourceHandler extends AbstractPUTRequestHandler<App> {

	private AppService appService;

	@Override
	protected void validateDocumentResource( IRI targetIRI, RDFResource requestDocumentResource ) {
		super.validateDocumentResource( targetIRI, requestDocumentResource );
		if ( ! AppFactory.getInstance().is( requestDocumentResource ) ) throw new InvalidResourceException( new Infraction( 0x2007 ) );
	}

	@Override
	protected App getDocumentResourceView( RDFResource requestDocumentResource ) {
		return new App( requestDocumentResource );
	}

	@Override
	protected void replaceResource( IRI targetIRI, App documentResourceView ) {

		appService.replace( documentResourceView );
	}

	@Autowired
	public void setAppService( AppService appService ) {
		this.appService = appService;
	}
}