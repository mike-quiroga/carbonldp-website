package com.carbonldp.ldp.web;

import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;

@RequestHandler
public class BaseRDFPutRequestHandler extends AbstractRDFPutRequestHandler<RDFSource> {
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
