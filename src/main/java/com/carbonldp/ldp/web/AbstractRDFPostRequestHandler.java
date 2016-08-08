package com.carbonldp.ldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.descriptions.APIPreferences.InteractionModel;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.InvalidResourceIRIException;
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
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.AbstractModel;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.joda.time.DateTime;
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

		IRI targetIRI = getTargetIRI( request );
		if ( ! targetResourceExists( targetIRI ) ) throw new NotFoundException();

		RDFResource requestDocumentResource = document.getDocumentResource();

		InteractionModel interactionModel = getInteractionModel( targetIRI );

		switch ( interactionModel ) {
			case RDF_SOURCE:
				return handlePOSTToRDFSource( targetIRI, requestDocumentResource );
			case CONTAINER:
				return handlePOSTToContainer( targetIRI, requestDocumentResource );
			default:
				throw new IllegalStateException();
		}

	}

	protected APIPreferences.InteractionModel getDefaultInteractionModel() {
		return APIPreferences.InteractionModel.CONTAINER;
	}

	private ResponseEntity<Object> handlePOSTToRDFSource( IRI targetIRI, RDFResource requestDocumentResource ) {
		AccessPoint requestAccessPoint = getRequestAccessPoint( requestDocumentResource );

		requestDocumentResource = getDocumentResourceWithFinalIRI( requestAccessPoint, targetIRI.stringValue() );
		if ( ! requestDocumentResource.equals( requestAccessPoint.getIRI() ) ) {
			requestAccessPoint = AccessPointFactory.getInstance().getAccessPoint( requestDocumentResource );
		}

		DateTime creationTime = sourceService.createAccessPoint( targetIRI, requestAccessPoint );

		return generateCreatedResponse( requestAccessPoint );
	}

	private AccessPoint getRequestAccessPoint( RDFResource requestDocumentResource ) {
		for ( RDFNodeEnum invalidType : invalidTypesForRDFSources ) {
			if ( requestDocumentResource.hasType( invalidType ) )
				throw new BadRequestException( new Infraction( 0x200C, "rdf.type", invalidType.getIRI().stringValue() ) );
		}
		if ( ! AccessPointFactory.getInstance().isAccessPoint( requestDocumentResource ) )
			throw new BadRequestException( 0x2104 );
		validateSystemManagedProperties( requestDocumentResource );
		return AccessPointFactory.getInstance().getAccessPoint( requestDocumentResource );
	}

	private ResponseEntity<Object> handlePOSTToContainer( IRI targetIRI, RDFResource requestDocumentResource ) {

		validateDocumentResource( targetIRI, requestDocumentResource );

		validateSystemManagedProperties( requestDocumentResource );
		BasicContainer requestBasicContainer = getRequestBasicContainer( requestDocumentResource );

		requestDocumentResource = getDocumentResourceWithFinalIRI( requestBasicContainer, targetIRI.stringValue() );
		if ( ! requestDocumentResource.equals( requestBasicContainer.getIRI() ) ) requestBasicContainer = new BasicContainer( requestDocumentResource );

		E documentResourceView = getDocumentResourceView( requestBasicContainer );

		createChild( targetIRI, documentResourceView );

		DateTime modified = sourceService.getModified( documentResourceView.getIRI() );
		return generateCreatedResponse( documentResourceView );
	}

	private void validateSystemManagedProperties( RDFResource resource ) {
		List<Infraction> infractions = ContainerFactory.getInstance().validateSystemManagedProperties( resource );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	private BasicContainer getRequestBasicContainer( RDFResource requestDocumentResource ) {
		for ( RDFNodeEnum invalidType : invalidTypesForContainers ) {
			if ( requestDocumentResource.hasType( invalidType ) )
				throw new BadRequestException( new Infraction( 0x200C, "rdf.type", invalidType.getIRI().stringValue() ) );
		}
		BasicContainer basicContainer;

		if ( ( ! ( requestDocumentResource.hasType( ContainerDescription.Resource.CLASS ) || requestDocumentResource.hasType( BasicContainerDescription.Resource.CLASS ) ) && ( ! hasInteractionModel( requestDocumentResource ) ) ) ) {
			requestDocumentResource.add( RDFSourceDescription.Property.DEFAULT_INTERACTION_MODEL.getIRI(), InteractionModel.RDF_SOURCE.getIRI() );
		}
		basicContainer = BasicContainerFactory.getInstance().create( requestDocumentResource );

		return basicContainer;
	}

	private boolean hasInteractionModel( RDFResource requestDocumentResource ) {
		return requestDocumentResource.hasProperty( RDFSourceDescription.Property.DEFAULT_INTERACTION_MODEL );
	}

	protected abstract E getDocumentResourceView( BasicContainer requestBasicContainer );

	protected abstract void createChild( IRI targetIRI, E documentResourceView );

	protected ResponseEntity<Object> generateCreatedResponse( AccessPoint accessPointCreated ) {
		return generateCreatedResponse( (RDFResource) accessPointCreated );
	}

	protected ResponseEntity<Object> generateCreatedResponse( E childCreated ) {
		return generateCreatedResponse( (RDFResource) childCreated );
	}

	private ResponseEntity<Object> generateCreatedResponse( RDFResource resourceCreated ) {
		setStrongETagHeader( HTTPUtil.formatStrongEtag( ModelUtil.calculateETag( resourceCreated.getBaseModel() ) ) );
		setLocationHeader( resourceCreated );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.CREATED );
	}

	protected RDFResource getDocumentResourceWithFinalIRI( RDFResource documentResource, String parentIRI ) {
		if ( hasGenericRequestIRI( documentResource ) ) {
			IRI forgedIRI = forgeUniqueIRI( documentResource, parentIRI, request );
			documentResource = renameResource( documentResource, forgedIRI );
		} else {
			validateRequestResourceRelativeness( documentResource, parentIRI );
		}
		return documentResource;
	}

	protected IRI forgeUniqueIRI( RDFResource requestResource, String parentIRI, HttpServletRequest request ) {
		IRI uniqueIRI = forgeDocumentResourceIRI( requestResource, parentIRI, request );

		// TODO: Check that the resourceIRI is unique and if not forge another one
		if ( sourceService.exists( uniqueIRI ) ) throw new ConflictException( 0x2008 );

		return uniqueIRI;
	}

	protected IRI forgeDocumentResourceIRI( RDFResource documentResource, String parentIRI, HttpServletRequest request ) {
		StringBuilder uriBuilder = new StringBuilder();
		uriBuilder.append( parentIRI );

		if ( ! parentIRI.endsWith( SLASH ) ) uriBuilder.append( SLASH );

		uriBuilder.append( forgeSlug( documentResource, parentIRI, request ) );

		return SimpleValueFactory.getInstance().createIRI( uriBuilder.toString() );
	}

	private String forgeSlug( RDFResource documentResource, String parentIRI, HttpServletRequest request ) {
		String uriSlug = configurationRepository.getGenericRequestSlug( documentResource.getIRI().stringValue() );
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

	protected void validateRequestResourceRelativeness( RDFResource requestResource, String targetIRI ) {
		String resourceIRI = requestResource.getIRI().stringValue();
		targetIRI = targetIRI.endsWith( SLASH ) ? targetIRI : targetIRI.concat( SLASH );
		if ( ! resourceIRI.startsWith( targetIRI ) ) {
			throw new InvalidResourceIRIException();
		}

		String relativeIRI = resourceIRI.replace( targetIRI, EMPTY_STRING );
		if ( relativeIRI.length() == 0 ) {
			throw new BadRequestException( 0x2203 );
		}

		int slashIndex = relativeIRI.indexOf( SLASH );
		if ( slashIndex == - 1 ) {
			if ( configurationRepository.enforceEndingSlash() ) {
				throw new BadRequestException( 0x200A );
			}
		}

		if ( ( slashIndex + 1 ) < relativeIRI.length() ) {
			throw new BadRequestException( 0x2009 );
		}

		if ( sourceService.exists( requestResource.getIRI() ) ) throw new ConflictException( 0x2008 );
	}

	protected RDFResource renameResource( RDFResource requestResource, IRI forgedIRI ) {
		AbstractModel renamedModel = ModelUtil.replaceBase( requestResource.getBaseModel(), requestResource.getIRI().stringValue(), forgedIRI.stringValue() );
		return new RDFResource( renamedModel, forgedIRI );
	}

}