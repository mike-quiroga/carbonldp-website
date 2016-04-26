package com.carbonldp.ldp.nonrdf;

import com.carbonldp.Consts;
import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.http.Link;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.IRI;

import org.openrdf.model.impl.SimpleValueFactory;
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
			case CONTAINER:
				return handlePOSTToContainer( targetIRI, file, contentType );
			default:
				throw new BadRequestException( 0x4002 );
		}
	}

	protected APIPreferences.InteractionModel getDefaultInteractionModel() {
		return APIPreferences.InteractionModel.CONTAINER;
	}

	private ResponseEntity<Object> handlePOSTToContainer( IRI targetIRI, File requestEntity, String contentType ) {
		IRI resourceIRI = forgeIRI( targetIRI, request );

		containerService.createNonRDFResource( targetIRI, resourceIRI, requestEntity, contentType );

		return generateCreatedResponse( resourceIRI );
	}

	private ResponseEntity<Object> generateCreatedResponse( IRI resourceIRI ) {
		response.setHeader( HTTPHeaders.LOCATION, resourceIRI.stringValue() );
		setStrongETagHeader( sourceService.getETag( resourceIRI ) );

		addDescribedByHeader( response, resourceIRI );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.CREATED );
	}

	private void addDescribedByHeader( HttpServletResponse response, IRI resourceIRI ) {
		Link link = new Link( resourceIRI.stringValue() );
		link.addRelationshipType( Consts.DESCRIBED_BY );
		link.setAnchor( resourceIRI.stringValue() );

		response.addHeader( HTTPHeaders.LINK, link.toString() );
	}

	private IRI forgeIRI( IRI parentIRI, HttpServletRequest request ) {
		String parentIRIString = parentIRI.stringValue();
		String slug = request.getHeader( HTTPHeaders.SLUG );
		if ( slug == null || slug.isEmpty() ) slug = IRIUtil.createRandomSlug();
		if ( parentIRIString.endsWith( Consts.SLASH ) ) slug = parentIRIString.concat( slug );
		else slug = parentIRIString.concat( Consts.SLASH + slug );
		if ( ! slug.endsWith( Consts.SLASH ) ) slug += Consts.SLASH;

		return SimpleValueFactory.getInstance().createIRI( slug );
	}

}
