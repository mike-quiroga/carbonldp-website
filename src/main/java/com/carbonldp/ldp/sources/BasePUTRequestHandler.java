package com.carbonldp.ldp.sources;

import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;

@RequestHandler( "rdfSource:basePUTRequestHandler" )
public class BasePUTRequestHandler extends AbstractPUTRequestHandler<RDFSource> {
	@Override
	protected RDFSource getDocumentResourceView( RDFResource requestDocumentResource ) {
		return new RDFSource( requestDocumentResource );
	}

	@Override
	protected void replaceResource( URI targetURI, RDFSource documentResourceView ) {
		sourceService.replace( documentResourceView );
	}

}
