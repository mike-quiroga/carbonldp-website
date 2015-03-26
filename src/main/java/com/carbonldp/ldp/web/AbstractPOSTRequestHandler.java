package com.carbonldp.ldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences.InteractionModel;
import com.carbonldp.ldp.containers.*;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.HTTPUtil;
import com.carbonldp.utils.ModelUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.ConflictException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.URIImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

import static com.carbonldp.Consts.EMPTY_STRING;
import static com.carbonldp.Consts.SLASH;

public abstract class AbstractPOSTRequestHandler extends AbstractRequestWithBodyHandler {

	private final static RDFNodeEnum[] invalidTypesForRDFSources;

	static {
		//@formatter:off
		List<? extends RDFNodeEnum> invalidTypes = Arrays.asList(
				BasicContainerDescription.Resource.CLASS
				// TODO: Add LDPNR, NRWRAPPER
		);
		//@formatter:on

		invalidTypesForRDFSources = invalidTypes.toArray( new RDFNodeEnum[invalidTypes.size()] );
	}

	private final static RDFNodeEnum[] invalidTypesForContainers;

	static {
		//@formatter:off
		List<? extends RDFNodeEnum> invalidTypes = Arrays.asList(
				DirectContainerDescription.Resource.CLASS,
				IndirectContainerDescription.Resource.CLASS
				// TODO: Add LDPNR, NRWRAPPER
		);
		//@formatter:on

		invalidTypesForContainers = invalidTypes.toArray( new RDFNodeEnum[invalidTypes.size()] );
	}

	public AbstractPOSTRequestHandler() {
		Set<InteractionModel> supportedInteractionModels = new HashSet<>();
		supportedInteractionModels.add( InteractionModel.RDF_SOURCE );
		supportedInteractionModels.add( InteractionModel.CONTAINER );
		setSupportedInteractionModels( supportedInteractionModels );

		setDefaultInteractionModel( InteractionModel.CONTAINER );
	}

	@Transactional
	public ResponseEntity<Object> handleRequest( AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) {
			throw new NotFoundException( "The target resource wasn't found." );
		}

		validateRequestModel( requestModel );

		RDFResource requestDocumentResource = getRequestDocumentResource( requestModel );
		seekForOrphanFragments( requestModel, requestDocumentResource );

		InteractionModel interactionModel = getInteractionModel( targetURI );

		switch ( interactionModel ) {
			case RDF_SOURCE:
				return handlePOSTToRDFSource( targetURI, requestDocumentResource );
			case CONTAINER:
				return handlePOSTToContainer( targetURI, requestDocumentResource );
			default:
				throw new IllegalStateException();
		}

	}

	private ResponseEntity<Object> handlePOSTToRDFSource( final URI targetURI, RDFResource requestDocumentResource ) {
		AccessPoint requestAccessPoint = processDocumentResource( requestDocumentResource, resource -> {
			for ( RDFNodeEnum invalidType : invalidTypesForRDFSources ) {
				if ( resource.hasType( invalidType ) )
					throw new BadRequestException( "One of the resources sent in the request contains an invalid type." );
			}
			if ( ! AccessPointFactory.isAccessPoint( resource ) )
				throw new BadRequestException( "RDFSource interaction model can only create AccessPoints." );
			if ( ! AccessPointFactory.isValid( resource, targetURI, false ) )
				throw new BadRequestException( "An AccessPoint sent isn't valid." );
			// TODO: Check for system managed properties
			return AccessPointFactory.getAccessPoint( resource );
		} );

		requestDocumentResource = getDocumentResourceWithFinalURI( requestAccessPoint, targetURI.stringValue() );
		if ( ! requestDocumentResource.equals( requestAccessPoint.getURI() ) ) {
			requestAccessPoint = AccessPointFactory.getAccessPoint( requestDocumentResource );
		}

		DateTime creationTime = sourceService.createAccessPoint( targetURI, requestAccessPoint );

		return createCreatedResponse( requestAccessPoint, creationTime );
	}

	private ResponseEntity<Object> handlePOSTToContainer( URI targetURI, RDFResource requestDocumentResource ) {
		BasicContainer requestBasicContainer = processDocumentResource( requestDocumentResource, resource -> {
			for ( RDFNodeEnum invalidType : invalidTypesForContainers ) {
				if ( resource.hasType( invalidType ) )
					throw new BadRequestException( "One of the resources sent in the request contains an invalid type." );
			}
			if ( BasicContainerFactory.isBasicContainer( resource ) ) {
				if ( ! BasicContainerFactory.isValid( resource ) )
					throw new BadRequestException( "A BasicContainer sent isn't valid." );
				// TODO: Check for system managed properties
				return new BasicContainer( resource );
			} else {
				BasicContainer basicContainer = BasicContainerFactory.create( resource );
				basicContainer.setDefaultInteractionModel( InteractionModel.RDF_SOURCE );
				return basicContainer;
			}
		} );

		requestDocumentResource = getDocumentResourceWithFinalURI( requestBasicContainer, targetURI.stringValue() );
		if ( ! requestDocumentResource.equals( requestBasicContainer.getURI() ) ) requestBasicContainer = new BasicContainer( requestDocumentResource );

		DateTime creationTime = containerService.createChild( targetURI, requestBasicContainer );

		return createCreatedResponse( requestDocumentResource, creationTime );
	}

	protected ResponseEntity<Object> createCreatedResponse( RDFResource createdResource, DateTime creationTime ) {
		response.setHeader( HTTPHeaders.LOCATION, createdResource.getURI().stringValue() );
		response.setHeader( HTTPHeaders.ETAG, HTTPUtil.formatWeakETag( creationTime.toString() ) );

		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.CREATED );
	}

	protected RDFResource getDocumentResourceWithFinalURI( RDFResource documentResource, String parentURI ) {
		if ( hasGenericRequestURI( documentResource ) ) {
			URI forgedURI = forgeUniqueURI( documentResource, parentURI, request );
			documentResource = renameResource( documentResource, forgedURI );
		} else {
			validateRequestResourceRelativeness( documentResource, parentURI );
		}
		return documentResource;
	}

	protected URI forgeUniqueURI( RDFResource requestResource, String parentURI, HttpServletRequest request ) {
		URI uniqueURI = forgeDocumentResourceURI( requestResource, parentURI, request );

		// TODO: Check that the resourceURI is unique and if not forge another one
		if ( sourceService.exists( uniqueURI ) ) throw new ConflictException( "The URI is already in use." );

		return uniqueURI;
	}

	protected URI forgeDocumentResourceURI( RDFResource documentResource, String parentURI, HttpServletRequest request ) {
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append( parentURI );

		if ( ! parentURI.endsWith( SLASH ) ) uriBuilder.append( SLASH );

		uriBuilder.append( forgeSlug( documentResource, parentURI, request ) );

		return new URIImpl( uriBuilder.toString() );
	}

	private String forgeSlug( RDFResource documentResource, String parentURI, HttpServletRequest request ) {
		String uriSlug = configurationRepository.getGenericRequestSlug( documentResource.getURI().stringValue() );
		String slug = uriSlug != null ? uriSlug : request.getHeader( HTTPHeaders.SLUG );

		if ( slug != null ) {
			if ( slug.endsWith( SLASH ) ) {
				slug = slug.substring( 0, slug.length() - 1 );
				slug = HTTPUtil.createSlug( slug ).concat( SLASH );
			} else slug = HTTPUtil.createSlug( slug );
		} else {
			Random random = new Random();
			slug = String.valueOf( Math.abs( random.nextLong() ) );
		}

		if ( configurationRepository.enforceEndingSlash() && ! slug.endsWith( SLASH ) ) slug = slug.concat( SLASH );

		return slug;
	}

	protected void validateRequestResourceRelativeness( RDFResource requestResource, String targetURI ) {
		String resourceURI = requestResource.getURI().stringValue();
		targetURI = targetURI.endsWith( SLASH ) ? targetURI : targetURI.concat( SLASH );
		if ( ! resourceURI.startsWith( targetURI ) ) {
			throw new BadRequestException( "A request resource's URI doesn't have the request URI as a base." );
		}

		String relativeURI = resourceURI.replace( targetURI, EMPTY_STRING );
		if ( relativeURI.length() == 0 ) {
			throw new BadRequestException( "A request resource's URI is the same as the request URI. Remember POST to parent, PUT to me." );
		}

		int slashIndex = relativeURI.indexOf( SLASH );
		if ( slashIndex == - 1 ) {
			if ( configurationRepository.enforceEndingSlash() ) {
				throw new BadRequestException( "A request resource's URI doesn't end up in a slash." );
			}
		}

		if ( ( slashIndex + 1 ) < relativeURI.length() ) {
			throw new BadRequestException( "A request resource's URI isn't an immediate child of the request URI." );
		}

		if ( sourceService.exists( requestResource.getURI() ) ) throw new ConflictException( "The URI is already in use." );
	}

	protected RDFResource renameResource( RDFResource requestResource, URI forgedURI ) {
		AbstractModel renamedModel = ModelUtil.replaceBase( requestResource.getBaseModel(), requestResource.getURI()
																										   .stringValue(), forgedURI
			.stringValue() );
		return new RDFResource( renamedModel, forgedURI );
	}

}