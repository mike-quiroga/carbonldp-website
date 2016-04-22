package com.carbonldp.ldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.descriptions.APIPreferences.ContainerRetrievalPreference;
import com.carbonldp.descriptions.APIPreferences.InteractionModel;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerDescription;
import com.carbonldp.ldp.containers.ContainerFactory;
import com.carbonldp.ldp.nonrdf.RDFRepresentation;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.models.HTTPHeader;
import com.carbonldp.models.HTTPHeaderValue;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.IRI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractGETRequestHandler extends AbstractLDPRequestHandler {

	private static final Set<ContainerRetrievalPreference> DEFAULT_RCP;

	static {
		DEFAULT_RCP = new HashSet<>();
		DEFAULT_RCP.add( ContainerRetrievalPreference.CONTAINER_PROPERTIES );
		DEFAULT_RCP.add( ContainerRetrievalPreference.MEMBERSHIP_TRIPLES );
		DEFAULT_RCP.add( ContainerRetrievalPreference.NON_READABLE_MEMBERSHIP_RESOURCE_TRIPLES );
	}

	public AbstractGETRequestHandler() {
		Set<InteractionModel> supportedInteractionModels = new HashSet<>();
		supportedInteractionModels.add( InteractionModel.RDF_SOURCE );
		supportedInteractionModels.add( InteractionModel.CONTAINER );
		supportedInteractionModels.add( InteractionModel.NON_RDF_SOURCE );
		supportedInteractionModels.add( InteractionModel.SPARQL_ENDPOINT );
		setSupportedInteractionModels( supportedInteractionModels );

		setDefaultInteractionModel( InteractionModel.CONTAINER );
	}

	@Transactional
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		IRI targetIRI = getTargetIRI( request );
		if ( ! sourceService.exists( targetIRI ) ) throw new NotFoundException();

		InteractionModel interactionModel = getInteractionModel( targetIRI );
		switch ( interactionModel ) {
			case RDF_SOURCE:
				return handleRDFSourceRetrieval( targetIRI );
			case CONTAINER:
				return handleContainerRetrieval( targetIRI );
			case NON_RDF_SOURCE:
				return handleNonRDFRetrieval( targetIRI );
			case SPARQL_ENDPOINT:
				return handleSPARQLEndpointRetrieval( targetIRI );
			default:
				throw new IllegalStateException();
		}
	}

	protected APIPreferences.InteractionModel getDefaultInteractionModel() {
		return InteractionModel.RDF_SOURCE;
	}

	protected ResponseEntity<Object> handleRDFSourceRetrieval( IRI targetIRI ) {
		// TODO: Take into account preferences (ACL, System Managed Properties, etc.)

		RDFSource source = sourceService.get( targetIRI );

		addRDFSourceAllowHeaders( targetIRI, response );

		setAppliedPreferenceHeaders();
		ContainerDescription.Type containerType = containerService.getContainerType( targetIRI );

		if ( containerType != null ) addContainerTypeLinkHeader( containerType );
		addTypeLinkHeader( InteractionModel.RDF_SOURCE );
		addInteractionModelLinkHeader( InteractionModel.RDF_SOURCE );
		return new ResponseEntity<>( source, HttpStatus.OK );
	}

	private void addRDFSourceAllowHeaders( IRI targetIRI, HttpServletResponse response ) {
		// TODO: Base this on the security model
		response.addHeader( HTTPHeaders.ALLOW, "GET, POST, PUT, PATCH, DELETE, OPTIONS" );

		response.addHeader( HTTPHeaders.ACCEPT_POST, "application/ld+json, text/turtle" );
		response.addHeader( HTTPHeaders.ACCEPT_PUT, "application/ld+json, text/turtle" );
		response.addHeader( HTTPHeaders.ACCEPT_PATCH, "application/ld+json, text/turtle" );
	}

	protected ResponseEntity<Object> handleContainerRetrieval( IRI targetIRI ) {
		Set<ContainerRetrievalPreference> containerRetrievalPreferences = getContainerRetrievalPreferences( targetIRI );

		Container container = containerService.get( targetIRI, containerRetrievalPreferences );

		// TODO: Add Container related information to the request (number of contained resources and members)

		addContainerAllowHeaders( targetIRI, response );

		setAppliedPreferenceHeaders();
		ContainerDescription.Type containerType = ContainerFactory.getInstance().getContainerType( container );
		if ( containerType == null ) containerType = containerService.getContainerType( container.getIRI() );

		addContainerTypeLinkHeader( containerType );
		addInteractionModelLinkHeader( containerType );

		return new ResponseEntity<>( container, HttpStatus.OK );
	}

	private void addContainerAllowHeaders( IRI targetIRI, HttpServletResponse response ) {
		// TODO: Base this on the security model
		response.addHeader( HTTPHeaders.ALLOW, "GET, POST, PUT, PATCH, DELETE, OPTIONS" );

		response.addHeader( HTTPHeaders.ACCEPT_POST, "application/ld+json, text/turtle" );
		response.addHeader( HTTPHeaders.ACCEPT_PUT, "application/ld+json, text/turtle" );
		response.addHeader( HTTPHeaders.ACCEPT_PATCH, "application/ld+json, text/turtle" );
	}

	private Set<ContainerRetrievalPreference> getContainerRetrievalPreferences( IRI targetIRI ) {
		Set<ContainerRetrievalPreference> preferences = new HashSet<>();
		Set<ContainerRetrievalPreference> defaultPreferences = getDefaultContainerRetrievalPreferences();
		Set<ContainerRetrievalPreference> containerDefinedPreferences = containerService.getRetrievalPreferences( targetIRI );

		if ( containerDefinedPreferences.isEmpty() ) preferences.addAll( defaultPreferences );
		else preferences.addAll( containerDefinedPreferences );

		return getContainerRetrievalPreferences( preferences, request );
	}

	private Set<ContainerRetrievalPreference> getContainerRetrievalPreferences( Set<ContainerRetrievalPreference> defaultPreferences, HttpServletRequest request ) {
		HTTPHeader preferHeader = new HTTPHeader( request.getHeaders( HTTPHeaders.PREFER ) );
		List<HTTPHeaderValue> includePreferences = HTTPHeader.filterHeaderValues( preferHeader, "return", "representation", "include", null );
		List<HTTPHeaderValue> omitPreferences = HTTPHeader.filterHeaderValues( preferHeader, "return", "representation", "omit", null );

		Set<APIPreferences.ContainerRetrievalPreference> appliedPreferences = new HashSet<>();

		for ( HTTPHeaderValue omitPreference : omitPreferences ) {
			ContainerRetrievalPreference containerPreference = RDFNodeUtil.findByIRI( omitPreference.getExtendingValue(), ContainerRetrievalPreference.class );
			if ( containerPreference == null ) continue;

			// TODO: Add AppliedPreference Header
			appliedPreferences.add( containerPreference );
			if ( defaultPreferences.contains( containerPreference ) ) defaultPreferences.remove( containerPreference );
		}

		for ( HTTPHeaderValue includePreference : includePreferences ) {
			ContainerRetrievalPreference containerPreference = RDFNodeUtil.findByIRI( includePreference.getExtendingValue(), ContainerRetrievalPreference.class );
			if ( containerPreference == null ) continue;

			// TODO: Add AppliedPreference Header
			if ( appliedPreferences.contains( containerPreference ) ) throw new BadRequestException( 0x5001 );
			if ( ! defaultPreferences.contains( containerPreference ) ) defaultPreferences.add( containerPreference );
		}
		if ( ( ! includePreferences.isEmpty() ) || ( ! omitPreferences.isEmpty() ) ) {
			addReturnRepresentationHeader();
		}

		return defaultPreferences;
	}

	protected Set<ContainerRetrievalPreference> getDefaultContainerRetrievalPreferences() {
		return DEFAULT_RCP;
	}

	protected ResponseEntity<Object> handleNonRDFRetrieval( IRI targetIRI ) {
		isRDFRepresentation( targetIRI );
		RDFRepresentation rdfRepresentation = new RDFRepresentation( sourceService.get( targetIRI ) );
		File file = nonRdfSourceService.getResource( rdfRepresentation );

		addNonRDFAllowHeader( targetIRI, response );

		return new ResponseEntity<>( new RDFRepresentationFileWrapper( rdfRepresentation, file ), HttpStatus.OK );
	}

	protected void addNonRDFAllowHeader( IRI targetIRI, HttpServletResponse response ) {
		// TODO: Base this on the security model
		response.addHeader( HTTPHeaders.ALLOW, "GET, PUT, DELETE, OPTIONS" );

		response.addHeader( HTTPHeaders.ACCEPT_PUT, "*/*" );
	}

	private void isRDFRepresentation( IRI targetIRI ) {
		if ( ! nonRdfSourceService.isRDFRepresentation( targetIRI ) ) throw new BadRequestException( 0x4003 );
	}

	protected ResponseEntity<Object> handleSPARQLEndpointRetrieval( IRI targetIRI ) {
		// TODO: Implement it
		return new ResponseEntity<>( HttpStatus.NOT_IMPLEMENTED );
	}

	public class RDFRepresentationFileWrapper {
		private File file;
		private RDFRepresentation rdfRepresentation;

		RDFRepresentationFileWrapper( RDFRepresentation rdfRepresentation, File file ) {
			this.file = file;
			this.rdfRepresentation = rdfRepresentation;
		}

		public File getFile() {
			return file;
		}

		public RDFRepresentation getRdfRepresentation() {
			return rdfRepresentation;
		}
	}
}
