package com.carbonldp.ldp.web;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

@Transactional
public abstract class AbstractPUTRequestHandler<E extends RDFResource> extends AbstractRequestWithBodyHandler<E> {

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

		String requestETag = getRequestETag();
		checkPrecondition( targetURI, requestETag );
		validateRequestModel( requestModel );

		RDFResource requestDocumentResource = getRequestDocumentResource( requestModel );
		validateDocumentResource( targetURI, requestDocumentResource );
		E documentResourceView = getDocumentResourceView( requestDocumentResource );
		validateDocumentResourceView( documentResourceView );

		seekForOrphanFragments( requestModel, requestDocumentResource );

		APIPreferences.InteractionModel interactionModel = getInteractionModel( targetURI );
		switch ( interactionModel ) {
			case RDF_SOURCE:
				return handlePUTToRDFSource( targetURI, documentResourceView );
			case CONTAINER:
			case LDPNR:
			case WRAPPER_FOR_LDPNR:
			case SPARQL_ENDPOINT:
				throw new BadRequestException( "The interaction model provided isn't supported in PUT requests." );
			default:
				throw new IllegalStateException();
		}
	}

	@Override
	protected void validateDocumentResource( URI targetURI, RDFResource requestDocumentResource ) {
		super.validateDocumentResource( targetURI, requestDocumentResource );
		if ( ! targetURI.equals( requestDocumentResource.getURI() ) ) throw new BadRequestException( "The documentResource's URI, sent in the request, is different to the request URI. Remember POST to parent, PUT to me." );
	}

	protected abstract E getDocumentResourceView( RDFResource requestDocumentResource );

	protected abstract ResponseEntity<Object> handlePUTToRDFSource( URI targetURI, E requestDocumentResource );
}
