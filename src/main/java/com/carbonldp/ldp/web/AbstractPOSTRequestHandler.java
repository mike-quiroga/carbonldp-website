package com.carbonldp.ldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences.InteractionModel;
import com.carbonldp.descriptions.BasicContainerDescription;
import com.carbonldp.descriptions.DirectContainerDescription;
import com.carbonldp.descriptions.IndirectContainerDescription;
import com.carbonldp.descriptions.RDFNodeEnum;
import com.carbonldp.models.*;
import com.carbonldp.utils.HTTPUtil;
import com.carbonldp.utils.ModelUtil;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.joda.time.DateTime;
import org.openrdf.model.Resource;
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

public abstract class AbstractPOSTRequestHandler extends AbstractLDPRequestHandler {

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

	@Transactional
	public ResponseEntity<Object> handleRequest(AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response) {
		setUp( request, response );

		URI targetURI = getTargetURI( request );
		if ( !targetResourceExists( targetURI ) ) {
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

	private ResponseEntity<Object> handlePOSTToRDFSource(final URI targetURI, RDFResource requestDocumentResource) {
		AccessPoint requestAccessPoint = processDocumentResource( requestDocumentResource, new ResourceProcessor<AccessPoint>() {
			@Override
			public AccessPoint processResource(RDFResource resource) {
				for ( RDFNodeEnum invalidType : invalidTypesForRDFSources ) {
					if ( resource.hasType( invalidType ) )
						throw new BadRequestException( "One of the resources sent in the request contains an invalid type." );
				}
				if ( !AccessPointFactory.isAccessPoint( resource ) )
					throw new BadRequestException( "RDFSource interaction model can only create AccessPoints." );
				if ( !AccessPointFactory.isValid( resource, targetURI, false ) )
					throw new BadRequestException( "An AccessPoint sent isn't valid." );
				// TODO: Check for system managed properties
				return AccessPointFactory.getAccessPoint( resource );
			}
		} );

		requestDocumentResource = getDocumentResourceWithFinalURI( requestAccessPoint, targetURI.stringValue() );
		if ( !requestDocumentResource.equals( requestAccessPoint.getURI() ) ) {
			requestAccessPoint = AccessPointFactory.getAccessPoint( requestDocumentResource );
		}

		DateTime now = DateTime.now();
		requestAccessPoint.setTimestamps( now );
		sourceService.createAccessPoint( targetURI, requestAccessPoint );
		sourceService.touch( targetURI, now );

		return createCreatedResponse( requestAccessPoint, now );
	}

	private ResponseEntity<Object> handlePOSTToContainer(URI targetURI, RDFResource requestDocumentResource) {
		BasicContainer requestBasicContainer = processDocumentResource( requestDocumentResource, new ResourceProcessor<BasicContainer>() {
			@Override
			public BasicContainer processResource(RDFResource resource) {
				for ( RDFNodeEnum invalidType : invalidTypesForContainers ) {
					if ( resource.hasType( invalidType ) )
						throw new BadRequestException( "One of the resources sent in the request contains an invalid type." );
				}
				if ( BasicContainerFactory.isBasicContainer( resource ) ) {
					if ( !BasicContainerFactory.isValid( resource ) )
						throw new BadRequestException( "A BasicContainer sent isn't valid." );
					// TODO: Check for system managed properties
					return new BasicContainer( resource );
				} else {
					BasicContainer basicContainer = BasicContainerFactory.create( resource );
					basicContainer.setDefaultInteractionModel( InteractionModel.RDF_SOURCE );
					return basicContainer;
				}
			}
		} );

		requestDocumentResource = getDocumentResourceWithFinalURI( requestBasicContainer, targetURI.stringValue() );
		if ( !requestDocumentResource.equals( requestBasicContainer.getURI() ) ) {
			requestBasicContainer = new BasicContainer( requestDocumentResource );
		}

		DateTime now = DateTime.now();
		requestBasicContainer.setTimestamps( now );
		containerService.createChild( targetURI, requestBasicContainer );
		sourceService.touch( targetURI, now );

		return createCreatedResponse( requestBasicContainer, now );
	}

	protected ResponseEntity<Object> createCreatedResponse(RDFResource createdResource, DateTime creationTime) {
		response.setHeader( HTTPHeaders.LOCATION, createdResource.getURI().stringValue() );
		response.setHeader( HTTPHeaders.ETAG, HTTPUtil.formatWeakETag( creationTime.toString() ) );

		return new ResponseEntity<Object>( new EmptyResponse(), HttpStatus.CREATED );
	}

	protected void validateRequestModel(AbstractModel requestModel) {
		Set<Resource> subjects = requestModel.subjects();
		validateRequestResourcesNumber( subjects.size() );

		for ( Resource subject : subjects )
			validateRequestResource( subject );
	}

	protected void validateRequestResourcesNumber(int number) {
		if ( number == 0 ) throw new BadRequestException( "The request doesn't contain rdf resources." );
	}

	protected void validateRequestResource(Resource subject) {
		if ( ValueUtil.isBNode( subject ) ) throw new BadRequestException( "Blank nodes are not supported." );
	}

	protected Set<RDFResource> getRequestDocumentResources(AbstractModel requestModel) {
		Set<RDFResource> documentResources = new HashSet<RDFResource>();
		for ( Resource subject : requestModel.subjects() ) {
			if ( !ValueUtil.isURI( subject ) ) continue;
			URI subjectURI = ValueUtil.getURI( subject );
			if ( URIUtil.hasFragment( subjectURI ) ) continue;
			RDFResource documentResource = new RDFResource( requestModel, subjectURI );
			documentResources.add( documentResource );
		}
		return documentResources;
	}

	protected RDFResource getRequestDocumentResource(AbstractModel requestModel) {
		RDFResource documentResource = null;
		for ( Resource subject : requestModel.subjects() ) {
			if ( !ValueUtil.isURI( subject ) ) continue;
			URI subjectURI = ValueUtil.getURI( subject );
			if ( URIUtil.hasFragment( subjectURI ) ) continue;
			if ( documentResource != null )
				throw new BadRequestException( "The request contains more than one document resource." );
			documentResource = new RDFResource( requestModel, subjectURI );
		}
		return documentResource;
	}

	protected <E extends RDFSource> Set<E> processDocumentResources(Set<RDFResource> requestDocumentResources, ResourceProcessor<E> resourceProcessor) {
		validateRequestDocumentResourcesNumber( requestDocumentResources.size() );

		Set<E> processedResources = new HashSet<E>();
		for ( RDFResource documentResource : requestDocumentResources ) {
			processedResources.add( resourceProcessor.processResource( documentResource ) );
		}
		return processedResources;
	}

	protected <E extends RDFSource> E processDocumentResource(RDFResource requestDocumentResource, ResourceProcessor<E> resourceProcessor) {
		if ( requestDocumentResource == null )
			throw new BadRequestException( "The request doesn't contain a document resource." );
		return resourceProcessor.processResource( requestDocumentResource );
	}

	protected void validateRequestDocumentResourcesNumber(int number) {
		if ( number == 0 ) throw new BadRequestException( "The request doesn't contain " );
	}

	protected void seekForOrphanFragments(AbstractModel requestModel, RDFResource requestDocumentResource) {
		for ( Resource subject : requestModel.subjects() ) {
			if ( !ValueUtil.isURI( subject ) ) continue;
			URI subjectURI = ValueUtil.getURI( subject );
			if ( !URIUtil.hasFragment( subjectURI ) ) continue;
			URI documentURI = new URIImpl( URIUtil.getDocumentURI( subjectURI.stringValue() ) );
			if ( !requestDocumentResource.getURI().equals( documentURI ) ) {
				throw new BadRequestException( "The request contains orphan fragments." );
			}
		}
	}

	// TODO: Optimize this
	protected void seekForOrphanFragments(AbstractModel requestModel, Set<RDFResource> requestDocumentResources) {
		for ( Resource subject : requestModel.subjects() ) {
			if ( !ValueUtil.isURI( subject ) ) continue;
			URI subjectURI = ValueUtil.getURI( subject );
			if ( !URIUtil.hasFragment( subjectURI ) ) continue;
			URI documentURI = new URIImpl( URIUtil.getDocumentURI( subjectURI.stringValue() ) );
			RDFResource fragmentResource = new RDFResource( requestModel, documentURI );
			if ( !requestDocumentResources.contains( fragmentResource ) ) {
				throw new BadRequestException( "All fragment resources must be accompanied by their document resource" );
			}
		}
	}

	protected RDFResource getDocumentResourceWithFinalURI(RDFResource documentResource, String parentURI) {
		if ( hasGenericRequestURI( documentResource ) ) {
			URI forgedURI = forgeUniqueURI( documentResource, parentURI, request );
			documentResource = renameResource( documentResource, forgedURI );
		} else {
			validateRequestResourceRelativeness( documentResource, parentURI );
		}
		return documentResource;
	}

	protected boolean hasGenericRequestURI(RDFResource resource) {
		return configurationRepository.isGenericRequest( resource.getURI().stringValue() );
	}

	protected URI forgeUniqueURI(RDFResource requestResource, String parentURI, HttpServletRequest request) {
		// TODO: Check that the resourceURI is unique and if not forge another one
		return forgeDocumentResourceURI( requestResource, parentURI, request );
	}

	protected URI forgeDocumentResourceURI(RDFResource documentResource, String parentURI, HttpServletRequest request) {
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append( parentURI );

		if ( !parentURI.endsWith( SLASH ) ) uriBuilder.append( SLASH );

		uriBuilder.append( forgeSlug( documentResource, parentURI, request ) );

		return new URIImpl( uriBuilder.toString() );
	}

	private String forgeSlug(RDFResource documentResource, String parentURI, HttpServletRequest request) {
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

		if ( configurationRepository.enforceEndingSlash() && !slug.endsWith( SLASH ) ) slug = slug.concat( SLASH );

		return slug;
	}

	protected void validateRequestResourceRelativeness(RDFResource requestResource, String targetURI) {
		String resourceURI = requestResource.getURI().stringValue();
		targetURI = targetURI.endsWith( SLASH ) ? targetURI : targetURI.concat( SLASH );
		if ( !resourceURI.startsWith( targetURI ) ) {
			throw new BadRequestException( "A request resource's URI doesn't have the request URI as a base." );
		}

		String relativeURI = resourceURI.replace( targetURI, EMPTY_STRING );
		if ( relativeURI.length() == 0 ) {
			throw new BadRequestException( "A request resource's URI is the same as the request URI. Remember POST to parent, PUT to me." );
		}

		int slashIndex = relativeURI.indexOf( SLASH );
		if ( slashIndex == -1 ) {
			if ( configurationRepository.enforceEndingSlash() ) {
				throw new BadRequestException( "A request resource's URI doesn't end up in a slash." );
			}
		}

		if ( (slashIndex + 1) < relativeURI.length() ) {
			throw new BadRequestException( "A request resource's URI isn't an immediate child of the request URI." );
		}
	}

	protected RDFResource renameResource(RDFResource requestResource, URI forgedURI) {
		AbstractModel renamedModel = ModelUtil.replaceBase( requestResource.getBaseModel(), requestResource.getURI()
																										   .stringValue(), forgedURI
				.stringValue() );
		return new RDFResource( renamedModel, forgedURI );
	}

	protected interface ResourceProcessor<E> {
		public E processResource(RDFResource resource);
	}

}