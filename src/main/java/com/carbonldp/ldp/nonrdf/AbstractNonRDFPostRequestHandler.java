package com.carbonldp.ldp.nonrdf;

import com.carbonldp.Consts;
import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.http.Link;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractNonRDFPostRequestHandler extends AbstractNonRDFRequestHandler {

	public AbstractNonRDFPostRequestHandler() {
		Set<APIPreferences.InteractionModel> supportedInteractionModels = new HashSet<>();
		supportedInteractionModels.add( APIPreferences.InteractionModel.CONTAINER );
		setSupportedInteractionModels( supportedInteractionModels );

		setDefaultInteractionModel( APIPreferences.InteractionModel.CONTAINER );
	}

	public ResponseEntity<Object> handleRequest( InputStream bodyInputStream, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );
		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) throw new NotFoundException();

		File file = createTemporaryFile( bodyInputStream );
		try {
			return handleRequest( file, targetURI );
		} finally {
			deleteTemporaryFile( file );
		}
	}

	private ResponseEntity<Object> handleRequest( File file, URI targetURI ) {
		String contentType = request.getContentType();
		if ( contentType == null ) throw new BadRequestException( 0x4004 );

		APIPreferences.InteractionModel interactionModel = getInteractionModel( targetURI );

		switch ( interactionModel ) {
			case CONTAINER:
				return handlePOSTToContainer( targetURI, file, contentType );
			default:
				throw new BadRequestException( 0x4002 );
		}
	}

	protected APIPreferences.InteractionModel getDefaultInteractionModel() {
		return APIPreferences.InteractionModel.CONTAINER;
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

		addDescribedByHeader( response, resourceURI );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.CREATED );
	}

	private void addDescribedByHeader( HttpServletResponse response, URI resourceURI ) {
		Link link = new Link( resourceURI.stringValue() );
		link.addRelationshipType( Consts.DESCRIBED_BY );
		link.setAnchor( resourceURI.stringValue() );

		response.addHeader( HTTPHeaders.LINK, link.toString() );
	}

	private URI forgeURI( URI parentURI, HttpServletRequest request ) {
		String parentURIString = parentURI.stringValue();
		String slug = request.getHeader( HTTPHeaders.SLUG );
		if ( slug == null || slug.isEmpty() ) slug = createRandomSlug();
		if ( parentURIString.endsWith( Consts.SLASH ) ) slug = parentURIString.concat( slug );
		else slug = parentURIString.concat( Consts.SLASH + slug );
		if ( ! slug.endsWith( Consts.SLASH ) ) slug += Consts.SLASH;

		return new URIImpl( slug );
	}

}
