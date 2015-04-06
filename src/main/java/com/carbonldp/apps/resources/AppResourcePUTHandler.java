package com.carbonldp.apps.resources;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.web.AbstractPUTRequestHandler;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.RequestHandler;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RequestHandler
public class AppResourcePUTHandler extends AbstractPUTRequestHandler<RDFSource> {
	@Override
	protected RDFSource getDocumentResourceView( RDFResource requestDocumentResource ) {
		return new RDFSource( requestDocumentResource );
	}

	@Override
	protected void validateDocumentResourceView( RDFSource documentResourceView ) {

	}

	@Override
	protected ResponseEntity<Object> handlePUTToRDFSource( URI targetURI, RDFSource requestDocumentResource ) {
		DateTime modified = sourceService.replace( requestDocumentResource );

		setETagHeader( modified );
		addTypeLinkHeader( APIPreferences.InteractionModel.RDF_SOURCE );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}

}
