package com.carbonldp.ldp.web;

import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.web.RequestHandler;
import org.openrdf.model.URI;

@RequestHandler
public class BaseRDFPostRequestHandler extends AbstractRDFPostRequestHandler<BasicContainer> {
	@Override
	protected BasicContainer getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return requestBasicContainer;
	}

	@Override
	protected void createChild( URI targetURI, BasicContainer documentResourceView ) {
		containerService.createChild( targetURI, documentResourceView );
	}

	@Override
	protected void validateDocumentResourceView( BasicContainer documentResourceView ) {}
}
