package com.carbonldp.ldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.exceptions.NotFoundException;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractNonRDFPostRequestHandler extends AbstractLDPRequestHandler {
	public ResponseEntity<Object> handleRequest( InputStream file, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );
		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) {
			throw new NotFoundException( "The target resource wasn't found." );
		}

		String contentTypeHeader = request.getHeader( HTTPHeaders.CONTENT_TYPE );

		APIPreferences.InteractionModel interactionModel = getInteractionModel( targetURI );

		switch ( interactionModel ) {
			case CONTAINER:
				return handleNonRDFResourcePOST( targetURI, contentTypeHeader, file );
			case RDF_SOURCE:
				throw new IllegalStateException();
			default:
				throw new IllegalStateException();
		}

	}

	public ResponseEntity<Object> handleMultipartRequest( MultipartFile multipartFileile, HttpServletRequest request, HttpServletResponse response ) throws IOException {
		File file = new File( multipartFileile.getOriginalFilename() );
		setUp( request, response );
		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) {
			throw new NotFoundException( "The target resource wasn't found." );
		}
		multipartFileile.transferTo( file );

		String contentTypeHeader = request.getHeader( HTTPHeaders.CONTENT_TYPE );

		APIPreferences.InteractionModel interactionModel = getInteractionModel( targetURI );
		switch ( interactionModel ) {
			case CONTAINER:
				return handleNonRDFResourcePOST( targetURI, contentTypeHeader, file );
			case RDF_SOURCE:
				throw new IllegalStateException();
			default:
				throw new IllegalStateException();
		}

	}

	private ResponseEntity<Object> handleNonRDFResourcePOST( URI targetURI, String contentType, File requestEntity ) {
		RDFResource slugUri = new RDFResource( targetURI );
		slugUri = getDocumentResourceWithFinalURI( slugUri, targetURI.stringValue() );

		DateTime creationTime = fileService.createFile( targetURI, slugUri, requestEntity );

	}
}
