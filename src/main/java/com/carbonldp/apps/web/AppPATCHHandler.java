package com.carbonldp.apps.web;

import com.carbonldp.ldp.sources.AbstractPATCHRequestHandler;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.IRI;

@RequestHandler
public class AppPATCHHandler extends AbstractPATCHRequestHandler {
	@Override
	protected void deleteResourceViews( IRI sourceIRI, RDFDocument document ) {
		super.deleteResourceViews( sourceIRI, document );
	}

	@Override
	protected void setResourceViews( IRI sourceIRI, RDFDocument document ) {
		super.setResourceViews( sourceIRI, document );
	}

	@Override
	protected void addResourceViews( IRI sourceIRI, RDFDocument document ) {
		super.addResourceViews( sourceIRI, document );
	}
}
