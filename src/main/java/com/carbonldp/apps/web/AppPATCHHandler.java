package com.carbonldp.apps.web;

import com.carbonldp.ldp.sources.AbstractPATCHRequestHandler;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;

@RequestHandler
public class AppPATCHHandler extends AbstractPATCHRequestHandler {
	@Override
	protected void deleteResourceViews( URI sourceURI, RDFDocument document ) {
		super.deleteResourceViews( sourceURI, document );
	}

	@Override
	protected void setResourceViews( URI sourceURI, RDFDocument document ) {
		super.setResourceViews( sourceURI, document );
	}

	@Override
	protected void addResourceViews( URI sourceURI, RDFDocument document ) {
		super.addResourceViews( sourceURI, document );
	}
}
