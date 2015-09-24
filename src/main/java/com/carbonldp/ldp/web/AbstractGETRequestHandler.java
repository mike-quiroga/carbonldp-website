package com.carbonldp.ldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.descriptions.APIPreferences.ContainerRetrievalPreference;
import com.carbonldp.descriptions.APIPreferences.InteractionModel;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.nonrdf.RDFRepresentation;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.models.HTTPHeader;
import com.carbonldp.models.HTTPHeaderValue;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
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

		URI targetURI = getTargetURI( request );
		if ( ! sourceService.exists( targetURI ) ) throw new NotFoundException();

		InteractionModel interactionModel = getInteractionModel( targetURI );
		switch ( interactionModel ) {
			case RDF_SOURCE:
				return handleRDFSourceRetrieval( targetURI );
			case CONTAINER:
				return handleContainerRetrieval( targetURI );
			case NON_RDF_SOURCE:
				return handleNonRDFRetrieval( targetURI );
			case SPARQL_ENDPOINT:
				return handleSPARQLEndpointRetrieval( targetURI );
			default:
				throw new IllegalStateException();
		}
	}

	protected ResponseEntity<Object> handleRDFSourceRetrieval( URI targetURI ) {
		// TODO: Take into account preferences (ACL, System Managed Properties, etc.)

		RDFSource source = sourceService.get( targetURI );

		addRDFSourceAllowHeaders( targetURI, response );

		setAppliedPreferenceHeaders();
		addTypeLinkHeader( InteractionModel.RDF_SOURCE );
		return new ResponseEntity<>( source, HttpStatus.OK );
	}

	private void addRDFSourceAllowHeaders( URI targetURI, HttpServletResponse response ) {
		// TODO: Base this on the security model
		response.addHeader( HTTPHeaders.ALLOW, "GET, POST, PUT, PATCH, DELETE, OPTIONS" );

		response.addHeader( HTTPHeaders.ACCEPT_POST, "application/ld+json, text/turtle" );
		response.addHeader( HTTPHeaders.ACCEPT_PUT, "application/ld+json, text/turtle" );
		response.addHeader( HTTPHeaders.ACCEPT_PATCH, "application/ld+json, text/turtle" );
	}

	protected ResponseEntity<Object> handleContainerRetrieval( URI targetURI ) {
		Set<ContainerRetrievalPreference> containerRetrievalPreferences = getContainerRetrievalPreferences( targetURI );

		Container container = containerService.get( targetURI, containerRetrievalPreferences );

		ensureETagIsPresent( container, containerRetrievalPreferences );

		// TODO: Add Container related information to the request (number of contained resources and members)

		addContainerAllowHeaders( targetURI, response );

		setAppliedPreferenceHeaders();
		addContainerTypeLinkHeader( container );
		return new ResponseEntity<>( container, HttpStatus.OK );
	}

	private void addContainerAllowHeaders( URI targetURI, HttpServletResponse response ) {
		// TODO: Base this on the security model
		response.addHeader( HTTPHeaders.ALLOW, "GET, POST, PUT, PATCH, DELETE, OPTIONS" );

		response.addHeader( HTTPHeaders.ACCEPT_POST, "application/ld+json, text/turtle" );
		response.addHeader( HTTPHeaders.ACCEPT_PUT, "application/ld+json, text/turtle" );
		response.addHeader( HTTPHeaders.ACCEPT_PATCH, "application/ld+json, text/turtle" );
	}

	private void ensureETagIsPresent( Container container, Set<ContainerRetrievalPreference> containerRetrievalPreferences ) {
		if ( ! containerRetrievalPreferences.contains( ContainerRetrievalPreference.CONTAINER_PROPERTIES ) ) {
			DateTime modified = sourceService.getModified( container.getURI() );
			if ( modified != null ) container.setETag( modified );
		}
	}

	private Set<ContainerRetrievalPreference> getContainerRetrievalPreferences( URI targetURI ) {
		Set<ContainerRetrievalPreference> preferences = new HashSet<>();
		Set<ContainerRetrievalPreference> defaultPreferences = getDefaultContainerRetrievalPreferences();
		Set<ContainerRetrievalPreference> containerDefinedPreferences = containerService.getRetrievalPreferences( targetURI );

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
			ContainerRetrievalPreference containerPreference = RDFNodeUtil.findByURI( omitPreference.getExtendingValue(), ContainerRetrievalPreference.class );
			if ( containerPreference == null ) continue;

			// TODO: Add AppliedPreference Header
			appliedPreferences.add( containerPreference );
			if ( defaultPreferences.contains( containerPreference ) ) defaultPreferences.remove( containerPreference );
		}

		for ( HTTPHeaderValue includePreference : includePreferences ) {
			ContainerRetrievalPreference containerPreference = RDFNodeUtil.findByURI( includePreference.getExtendingValue(), ContainerRetrievalPreference.class );
			if ( containerPreference == null ) continue;

			// TODO: Add AppliedPreference Header
			if ( appliedPreferences.contains( containerPreference ) ) throw new BadRequestException( 0x5001 );
			if ( ! defaultPreferences.contains( containerPreference ) ) defaultPreferences.add( containerPreference );
		}

		return defaultPreferences;
	}

	protected Set<ContainerRetrievalPreference> getDefaultContainerRetrievalPreferences() {
		return DEFAULT_RCP;
	}

	protected ResponseEntity<Object> handleNonRDFRetrieval( URI targetURI ) {
		isRDFRepresentation( targetURI );
		RDFRepresentation rdfRepresentation = new RDFRepresentation( sourceService.get( targetURI ) );
		File file = nonRdfSourceService.getResource( rdfRepresentation );

		addNonRDFAllowHeader( targetURI, response );

		return new ResponseEntity<>( new RDFRepresentationFileWrapper( rdfRepresentation, file ), HttpStatus.OK );
	}

	private void addNonRDFAllowHeader( URI targetURI, HttpServletResponse response ) {
		// TODO: Base this on the security model
		response.addHeader( HTTPHeaders.ALLOW, "GET, PUT, DELETE, OPTIONS" );

		response.addHeader( HTTPHeaders.ACCEPT_PUT, "*/*" );
	}

	private void isRDFRepresentation( URI targetURI ) {
		if ( ! nonRdfSourceService.isRDFRepresentation( targetURI ) ) throw new BadRequestException( 0x4003 );
	}

	protected ResponseEntity<Object> handleSPARQLEndpointRetrieval( URI targetURI ) {
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
