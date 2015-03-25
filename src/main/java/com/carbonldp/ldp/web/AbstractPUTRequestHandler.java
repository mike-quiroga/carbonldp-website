package com.carbonldp.ldp.web;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

@Transactional
public abstract class AbstractPUTRequestHandler extends AbstractRequestWithBodyHandler {

	public AbstractPUTRequestHandler() {
		Set<APIPreferences.InteractionModel> supportedInteractionModels = new HashSet<>();
		supportedInteractionModels.add( APIPreferences.InteractionModel.RDF_SOURCE );
		setSupportedInteractionModels( supportedInteractionModels );

		setDefaultInteractionModel( APIPreferences.InteractionModel.RDF_SOURCE );
	}

	public ResponseEntity<Object> handleRequest( AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) {
			throw new NotFoundException( "The target resource wasn't found." );
		}

		checkPrecondition( targetURI );
		validateRequestModel( requestModel );

		RDFResource requestDocumentResource = getRequestDocumentResource( requestModel );
		RDFSource requestSource = processDocumentResource( requestDocumentResource, documentResource -> {
			if ( ! targetURI.equals( documentResource.getURI() ) ) throw new BadRequestException( "The documentResource's URI, sent in the request, is different to the request URI. Remember POST to parent, PUT to me." );
			return new RDFSource( documentResource );
		} );

		seekForOrphanFragments( requestModel, requestDocumentResource );

		APIPreferences.InteractionModel interactionModel = getInteractionModel( targetURI );
		switch ( interactionModel ) {
			case RDF_SOURCE:
				return handlePUTToRDFSource( targetURI, requestSource );
			case CONTAINER:
			case LDPNR:
			case WRAPPER_FOR_LDPNR:
			case SPARQL_ENDPOINT:
				throw new BadRequestException( "The interaction model provided isn't supported in PUT requests." );
			default:
				throw new IllegalStateException();
		}
	}

	private ResponseEntity<Object> handlePUTToRDFSource( URI targetURI, RDFSource requestSource ) {
		DateTime modified = sourceService.replace( requestSource );

		setETagHeader( modified );
		addTypeLinkHeader( APIPreferences.InteractionModel.RDF_SOURCE );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}
}
