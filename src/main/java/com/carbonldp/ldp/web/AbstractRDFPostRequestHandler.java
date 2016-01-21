package com.carbonldp.ldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.descriptions.APIPreferences.InteractionModel;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.InvalidResourceURIException;
import com.carbonldp.ldp.containers.*;
import com.carbonldp.ldp.nonrdf.RDFRepresentationDescription;
import com.carbonldp.ldp.sources.RDFSourceDescription;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFDocument;
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

import static com.carbonldp.Consts.*;

@Transactional
public abstract class AbstractRDFPostRequestHandler<E extends BasicContainer> extends AbstractRequestWithBodyHandler<E> {

	private final static RDFNodeEnum[] invalidTypesForRDFSources;

	static {
		List<? extends RDFNodeEnum> invalidTypes = Arrays.asList(
			BasicContainerDescription.Resource.CLASS,
			RDFRepresentationDescription.Resource.NON_RDF_SOURCE
			// TODO: NRWRAPPER
		);
		invalidTypesForRDFSources = invalidTypes.toArray( new RDFNodeEnum[invalidTypes.size()] );
	}

	private final static RDFNodeEnum[] invalidTypesForContainers;

	static {
		List<? extends RDFNodeEnum> invalidTypes = Arrays.asList(
			DirectContainerDescription.Resource.CLASS,
			IndirectContainerDescription.Resource.CLASS
			// TODO: Add NON_RDF_SOURCE, NRWRAPPER
		);
		invalidTypesForContainers = invalidTypes.toArray( new RDFNodeEnum[invalidTypes.size()] );
	}

	public AbstractRDFPostRequestHandler() {
		Set<InteractionModel> supportedInteractionModels = new HashSet<>();
		supportedInteractionModels.add( InteractionModel.RDF_SOURCE );
		supportedInteractionModels.add( InteractionModel.CONTAINER );
		setSupportedInteractionModels( supportedInteractionModels );

		setDefaultInteractionModel( InteractionModel.CONTAINER );
	}

	public ResponseEntity<Object> handleRequest( RDFDocument document, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) throw new NotFoundException();

		RDFResource requestDocumentResource = document.getDocumentResource();

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

	protected APIPreferences.InteractionModel getDefaultInteractionModel() {
		return APIPreferences.InteractionModel.CONTAINER;
	}

	private ResponseEntity<Object> handlePOSTToRDFSource( URI targetURI, RDFResource requestDocumentResource ) {
		AccessPoint requestAccessPoint = getRequestAccessPoint( requestDocumentResource );

		requestDocumentResource = getDocumentResourceWithFinalURI( requestAccessPoint, targetURI.stringValue() );
		if ( ! requestDocumentResource.equals( requestAccessPoint.getURI() ) ) {
			requestAccessPoint = AccessPointFactory.getInstance().getAccessPoint( requestDocumentResource );
		}

		DateTime creationTime = sourceService.createAccessPoint( targetURI, requestAccessPoint );

		return generateCreatedResponse( requestAccessPoint, creationTime );
	}

	private AccessPoint getRequestAccessPoint( RDFResource requestDocumentResource ) {
		for ( RDFNodeEnum invalidType : invalidTypesForRDFSources ) {
			if ( requestDocumentResource.hasType( invalidType ) )
				throw new BadRequestException( new Infraction( 0x200C, "rdf.type", invalidType.getURI().stringValue() ) );
		}
		if ( ! AccessPointFactory.getInstance().isAccessPoint( requestDocumentResource ) )
			throw new BadRequestException( 0x2104 );
		validateSystemManagedProperties( requestDocumentResource );
		return AccessPointFactory.getInstance().getAccessPoint( requestDocumentResource );
	}

	private ResponseEntity<Object> handlePOSTToContainer( URI targetURI, RDFResource requestDocumentResource ) {

		validateDocumentResource( targetURI, requestDocumentResource );

		validateSystemManagedProperties( requestDocumentResource );
		BasicContainer requestBasicContainer = getRequestBasicContainer( requestDocumentResource );

		requestDocumentResource = getDocumentResourceWithFinalURI( requestBasicContainer, targetURI.stringValue() );
		if ( ! requestDocumentResource.equals( requestBasicContainer.getURI() ) ) requestBasicContainer = new BasicContainer( requestDocumentResource );

		E documentResourceView = getDocumentResourceView( requestBasicContainer );

		createChild( targetURI, documentResourceView );

		DateTime modified = sourceService.getModified( documentResourceView.getURI() );
		return generateCreatedResponse( documentResourceView, modified );
	}

	private void validateSystemManagedProperties( RDFResource resource ) {
		List<Infraction> infractions = ContainerFactory.getInstance().validateSystemManagedProperties( resource );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private BasicContainer getRequestBasicContainer( RDFResource requestDocumentResource ) {
		for ( RDFNodeEnum invalidType : invalidTypesForContainers ) {
			if ( requestDocumentResource.hasType( invalidType ) )
				throw new BadRequestException( new Infraction( 0x200C, "rdf.type", invalidType.getURI().stringValue() ) );
		}
		BasicContainer basicContainer;

		if ( ( ! ( requestDocumentResource.hasType( ContainerDescription.Resource.CLASS ) || requestDocumentResource.hasType( BasicContainerDescription.Resource.CLASS ) ) && ( ! hasInteractionModel( requestDocumentResource ) ) ) ) {
			requestDocumentResource.add( RDFSourceDescription.Property.DEFAULT_INTERACTION_MODEL.getURI(), InteractionModel.RDF_SOURCE.getURI() );
		}
		basicContainer = BasicContainerFactory.getInstance().create( requestDocumentResource );

		return basicContainer;
	}

	private boolean hasInteractionModel( RDFResource requestDocumentResource ) {
		return requestDocumentResource.hasProperty( RDFSourceDescription.Property.DEFAULT_INTERACTION_MODEL );
	}

	protected abstract E getDocumentResourceView( BasicContainer requestBasicContainer );

	protected abstract void createChild( URI targetURI, E documentResourceView );

	protected ResponseEntity<Object> generateCreatedResponse( AccessPoint accessPointCreated, DateTime creationTime ) {
		return generateCreatedResponse( (RDFResource) accessPointCreated, creationTime );
	}

	protected ResponseEntity<Object> generateCreatedResponse( E childCreated, DateTime creationTime ) {
		return generateCreatedResponse( (RDFResource) childCreated, creationTime );
	}

	private ResponseEntity<Object> generateCreatedResponse( RDFResource resourceCreated, DateTime creationTime ) {
		if ( creationTime != null ) setETagHeader( creationTime );
		setLocationHeader( resourceCreated );
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
		if ( sourceService.exists( uniqueURI ) ) throw new ConflictException( 0x2008 );

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
			throw new InvalidResourceURIException();
		}

		String relativeURI = resourceURI.replace( targetURI, EMPTY_STRING );
		if ( relativeURI.length() == 0 ) {
			throw new BadRequestException( 0x2203 );
		}

		int slashIndex = relativeURI.indexOf( SLASH );
		if ( slashIndex == - 1 ) {
			if ( configurationRepository.enforceEndingSlash() ) {
				throw new BadRequestException( 0x200A );
			}
		}

		if ( ( slashIndex + 1 ) < relativeURI.length() ) {
			throw new BadRequestException( 0x2009 );
		}

		if ( sourceService.exists( requestResource.getURI() ) ) throw new ConflictException( 0x2008 );
	}

	protected RDFResource renameResource( RDFResource requestResource, URI forgedURI ) {
		AbstractModel renamedModel = ModelUtil.replaceBase( requestResource.getBaseModel(), requestResource.getURI().stringValue(), forgedURI.stringValue() );
		return new RDFResource( renamedModel, forgedURI );
	}

}