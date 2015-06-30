package com.carbonldp.ldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public abstract class AbstractNonRDFPostRequestHandler extends AbstractLDPRequestHandler {

	public AbstractNonRDFPostRequestHandler() {
		Set<APIPreferences.InteractionModel> supportedInteractionModels = new HashSet<>();
		supportedInteractionModels.add( APIPreferences.InteractionModel.CONTAINER );
		setSupportedInteractionModels( supportedInteractionModels );

		setDefaultInteractionModel( APIPreferences.InteractionModel.CONTAINER );
	}

	public ResponseEntity<Object> handleRequest( InputStream bodyInputStream, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );
		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) throw new NotFoundException( "The target resource wasn't found." );

		File file = createTemporaryFile( bodyInputStream );
		try {
			return handleRequest( file, targetURI );
		} finally {
			deleteTemporaryFile( file );
		}
	}

	private ResponseEntity<Object> handleRequest( File file, URI targetURI ) {
		setUp( request, response );

		String contentType = request.getContentType();
		if ( contentType == null ) throw new BadRequestException( "A Content-Type needs to be specified." );

		APIPreferences.InteractionModel interactionModel = getInteractionModel( targetURI );

		switch ( interactionModel ) {
			case CONTAINER:
				return handlePOSTToContainer( targetURI, file, contentType );
			case RDF_SOURCE:
				throw new BadRequestException( "POSTing a NonRDFResource using an interaction model of ldp:RDFSource isn't supported." );
			default:
				throw new IllegalStateException();
		}
	}

	private File createTemporaryFile( InputStream inputStream ) {
		File temporaryFile;
		FileOutputStream outputStream = null;
		try {
			temporaryFile = File.createTempFile( createRandomSlug(), null );
			temporaryFile.deleteOnExit(); // TODO: See if this makes the VM log warnings/errors

			outputStream = new FileOutputStream( temporaryFile );
			IOUtils.copy( inputStream, outputStream );
		} catch ( IOException | SecurityException e ) {
			throw new RuntimeException( "The temporary file couldn't be created. Exception:", e );
		} finally {
			try {
				inputStream.close();
				outputStream.close();
			} catch ( IOException e ) {
				LOG.error( "The inputStream couldn't be closed. Exception: ", e );
			}
		}
		return temporaryFile;
	}

	private void deleteTemporaryFile( File file ) {
		boolean wasDeleted;
		try {
			wasDeleted = file.delete();
		} catch ( SecurityException e ) {
			LOG.error( "A temporary file couldn't be deleted. Exception:", e );
		}
	}

	private ResponseEntity<Object> handlePOSTToContainer( URI targetURI, File requestEntity, String contentType ) {
		URI resourceURI = forgeURI( targetURI, request );

		containerService.createNonRDFResource( targetURI, resourceURI, requestEntity, contentType );

		DateTime modified = sourceService.getModified( resourceURI );
		return generateCreatedResponse( resourceURI, modified );
	}

	private ResponseEntity<Object> generateCreatedResponse( URI resourceURI, DateTime creationTime ) {
		response.setHeader( HTTPHeaders.LOCATION, resourceURI.stringValue() );
		if ( creationTime != null ) setETagHeader( creationTime );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.CREATED );
	}

	private URI forgeURI( URI parentURI, HttpServletRequest request ) {
		String parentURIString = parentURI.stringValue();
		String slug = request.getHeader( "Slug" );
		if ( slug == null || slug.isEmpty() ) slug = createRandomSlug();
		if ( parentURIString.endsWith( "/" ) ) slug = parentURIString.concat( slug );
		else slug = parentURIString.concat( "/" + slug );
		if ( ! slug.endsWith( "/" ) ) slug += "/";

		return new URIImpl( slug );
	}

	private String createRandomSlug() {
		Random random = new Random();
		String slug = String.valueOf( Math.abs( random.nextLong() ) );
		return slug;
	}
}
