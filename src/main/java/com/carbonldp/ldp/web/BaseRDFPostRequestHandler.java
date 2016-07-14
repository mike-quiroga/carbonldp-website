package com.carbonldp.ldp.web;

import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;

@RequestHandler
public class BaseRDFPostRequestHandler extends AbstractRDFPostRequestHandler<BasicContainer> {
	@Override
	protected BasicContainer getDocumentResourceView( BasicContainer requestBasicContainer ) {
		return requestBasicContainer;
	}

	@Override
	protected void createChild( IRI targetIRI, BasicContainer documentResourceView ) {
		containerService.createChild( targetIRI, documentResourceView );
	}
}
