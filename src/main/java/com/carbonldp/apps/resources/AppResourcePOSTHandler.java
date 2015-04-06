package com.carbonldp.apps.resources;

import com.carbonldp.ldp.web.AbstractPOSTRequestHandler;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.RequestHandler;

@RequestHandler
public class AppResourcePOSTHandler extends AbstractPOSTRequestHandler {
	@Override
	protected void validateDocumentResourceView( RDFResource documentResourceView ) {}
}
