package com.carbonldp.ldp.sources;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.ldp.web.AbstractRequestWithBodyHandler;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.IRI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Transactional
public abstract class AbstractPUTRequestHandler<E extends RDFResource> extends AbstractRequestWithBodyHandler<E> {
	public ResponseEntity<Object> handleRequest( RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		IRI targetIRI = getTargetIRI( request );
		if ( ! targetResourceExists( targetIRI ) ) {
			throw new NotFoundException();
		}

		String requestETag = getRequestETag();
		checkPrecondition( targetIRI, requestETag );

		RDFResource requestDocumentResource = requestDocument.getDocumentResource();

		validateDocumentResource( targetIRI, requestDocumentResource );
		E documentResourceView = getDocumentResourceView( requestDocumentResource );

		replaceResource( targetIRI, documentResourceView );

		addTypeLinkHeader( APIPreferences.InteractionModel.RDF_SOURCE );
		return createSuccessfulResponse( targetIRI );
	}

	@Override
	protected void validateDocumentResource( IRI targetIRI, RDFResource requestDocumentResource ) {
		super.validateDocumentResource( targetIRI, requestDocumentResource );
		if ( ! targetIRI.equals( requestDocumentResource.getIRI() ) ) throw new BadRequestException( 0x2203 );
	}

	protected abstract E getDocumentResourceView( RDFResource requestDocumentResource );

	protected ResponseEntity<Object> createSuccessfulResponse( IRI affectedResourceIRI ) {
		String eTag = sourceService.getETag( affectedResourceIRI );

		setStrongETagHeader( eTag );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}

	protected abstract void replaceResource( IRI targetIRI, E documentResourceView );
}