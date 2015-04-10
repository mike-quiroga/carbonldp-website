package com.carbonldp.ldp.web;

import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;

@RequestHandler
public class BasePUTRequestHandler extends AbstractPUTRequestHandler<RDFSource> {
	@Override
	protected RDFSource getDocumentResourceView( RDFResource requestDocumentResource ) {
		return new RDFSource( requestDocumentResource );
	}

	@Override
	protected void replaceResource( URI targetURI, RDFSource documentResourceView ) {
		sourceService.replace( documentResourceView );
	}

	@Override
	protected void validateDocumentResourceView( RDFSource documentResourceView ) {

	}
}
