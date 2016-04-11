package com.carbonldp.ldp.nonrdf;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.IRI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

@Transactional
public class AbstractNonRDFPutRequestHandler extends AbstractNonRDFRequestHandler {

	public AbstractNonRDFPutRequestHandler() {
		Set<APIPreferences.InteractionModel> supportedInteractionModels = new HashSet<>();
		supportedInteractionModels.add( APIPreferences.InteractionModel.NON_RDF_SOURCE );
		setSupportedInteractionModels( supportedInteractionModels );

		setDefaultInteractionModel( APIPreferences.InteractionModel.NON_RDF_SOURCE );
	}

	public ResponseEntity<Object> handleRequest( InputStream bodyInputStream, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );
		IRI targetIRI = getTargetIRI( request );
		if ( ! targetResourceExists( targetIRI ) ) throw new NotFoundException();

		File file = createTemporaryFile( bodyInputStream );
		try {
			return handleRequest( file, targetIRI );
		} finally {
			deleteTemporaryFile( file );
		}
	}

	private ResponseEntity<Object> handleRequest( File file, IRI targetIRI ) {
		String contentType = request.getContentType();
		if ( contentType == null ) throw new BadRequestException( 0x4004 );

		APIPreferences.InteractionModel interactionModel = getInteractionModel( targetIRI );

		switch ( interactionModel ) {
			case NON_RDF_SOURCE:
				return handlePUTToContainer( targetIRI, file, contentType );
			default:
				throw new BadRequestException( 0x4002 );
		}
	}

	protected APIPreferences.InteractionModel getDefaultInteractionModel() {
		return APIPreferences.InteractionModel.NON_RDF_SOURCE;
	}

	private ResponseEntity<Object> handlePUTToContainer( IRI targetIRI, File requestEntity, String contentType ) {
		RDFRepresentation rdfRepresentation = new RDFRepresentation( sourceService.get( targetIRI ) );

		nonRdfSourceService.replace( rdfRepresentation, requestEntity, contentType );

		addTypeLinkHeader( RDFRepresentationDescription.Resource.CLASS );
		return createSuccessfulResponse( targetIRI );
	}

	protected ResponseEntity<Object> createSuccessfulResponse( IRI affectedResourceIRI ) {
		String eTag = sourceService.getETag( affectedResourceIRI );

		setStrongETagHeader( eTag );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}

}
