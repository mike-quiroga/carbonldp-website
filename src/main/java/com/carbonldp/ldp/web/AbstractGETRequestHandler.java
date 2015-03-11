package com.carbonldp.ldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences.ContainerRetrievalPreference;
import com.carbonldp.descriptions.APIPreferences.InteractionModel;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.models.HTTPHeader;
import com.carbonldp.models.HTTPHeaderValue;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.web.exceptions.NotFoundException;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class AbstractGETRequestHandler extends AbstractLDPRequestHandler {

	//@formatter:off
	private static final Set<ContainerRetrievalPreference> DEFAULT_RCP;

	static {
		DEFAULT_RCP = new HashSet<ContainerRetrievalPreference>();
		DEFAULT_RCP.add( ContainerRetrievalPreference.CONTAINER_PROPERTIES );
		DEFAULT_RCP.add( ContainerRetrievalPreference.MEMBERSHIP_TRIPLES );
	}
	//@formatter:on

	public AbstractGETRequestHandler() {
		Set<InteractionModel> supportedInteractionModels = new HashSet<InteractionModel>();
		supportedInteractionModels.add( InteractionModel.RDF_SOURCE );
		supportedInteractionModels.add( InteractionModel.CONTAINER );
		supportedInteractionModels.add( InteractionModel.SPARQL_ENDPOINT );
		supportedInteractionModels.add( InteractionModel.WRAPPER_FOR_LDPNR );
		setSupportedInteractionModels( supportedInteractionModels );

		setDefaultInteractionModel( InteractionModel.RDF_SOURCE );
	}

	@Transactional
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		URI targetURI = getTargetURI( request );
		if ( ! sourceService.exists( targetURI ) ) throw new NotFoundException( "The resource wasn't found" );

		InteractionModel interactionModel = getInteractionModel( targetURI );
		switch ( interactionModel ) {
			case WRAPPER_FOR_LDPNR:
			case RDF_SOURCE:
				return handleRDFSourceRetrieval( targetURI );
			case CONTAINER:
				return handleContainerRetrieval( targetURI );
			case LDPNR:
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

		// TODO: Add Allow Headers (depending on security)

		setAppliedPreferenceHeaders();
		return new ResponseEntity<Object>( source, HttpStatus.OK );
	}

	protected ResponseEntity<Object> handleContainerRetrieval( URI targetURI ) {
		Set<ContainerRetrievalPreference> containerRetrievalPreferences = getContainerRetrievalPreferences( targetURI );
		Container container = containerService.get( targetURI, containerRetrievalPreferences );

		ensureETagIsPresent( container, containerRetrievalPreferences );

		// TODO: Add Container related information to the request (number of contained resources and members)

		// TODO: Add Allow Headers (depending on security)

		setAppliedPreferenceHeaders();
		return new ResponseEntity<Object>( container, HttpStatus.OK );
	}

	private void ensureETagIsPresent( Container container, Set<ContainerRetrievalPreference> containerRetrievalPreferences ) {
		if ( ! containerRetrievalPreferences.contains( ContainerRetrievalPreference.CONTAINER_PROPERTIES ) ) {
			DateTime modified = sourceService.getModified( container.getURI() );
			container.setETag( modified );
		}
	}

	private Set<ContainerRetrievalPreference> getContainerRetrievalPreferences( URI targetURI ) {
		Set<ContainerRetrievalPreference> preferences = new HashSet<ContainerRetrievalPreference>();
		Set<ContainerRetrievalPreference> defaultPreferences = getDefaultContainerRetrievalPreferences();
		Set<ContainerRetrievalPreference> containerDefinedPreferences = containerService.getRetrievalPreferences( targetURI );

		if ( containerDefinedPreferences.isEmpty() ) preferences.addAll( defaultPreferences );
		else preferences.addAll( containerDefinedPreferences );

		return getContainerRetrievalPreferences( defaultPreferences, request );
	}

	private Set<ContainerRetrievalPreference> getContainerRetrievalPreferences( Set<ContainerRetrievalPreference> defaultPreferences, HttpServletRequest request ) {
		HTTPHeader preferHeader = new HTTPHeader( request.getHeaders( HTTPHeaders.PREFER ) );
		List<HTTPHeaderValue> includePreferences = HTTPHeader.filterHeaderValues( preferHeader, "return", "representation", "include", null );
		List<HTTPHeaderValue> omitPreferences = HTTPHeader.filterHeaderValues( preferHeader, "return", "representation", "omit", null );

		for ( HTTPHeaderValue omitPreference : omitPreferences ) {
			ContainerRetrievalPreference containerPreference = RDFNodeUtil.findByURI( omitPreference.getExtendingValue(), ContainerRetrievalPreference.class );
			if ( containerPreference != null ) {
				if ( defaultPreferences.contains( containerPreference ) ) {
					defaultPreferences.remove( containerPreference );
				}
			}
		}

		for ( HTTPHeaderValue includePreference : includePreferences ) {
			ContainerRetrievalPreference containerPreference = RDFNodeUtil.findByURI( includePreference.getExtendingValue(), ContainerRetrievalPreference.class );
			if ( containerPreference != null ) {
				if ( ! defaultPreferences.contains( containerPreference ) ) {
					defaultPreferences.add( containerPreference );
				}
			}
		}

		return defaultPreferences;
	}

	protected Set<ContainerRetrievalPreference> getDefaultContainerRetrievalPreferences() {
		return DEFAULT_RCP;
	}

	protected ResponseEntity<Object> handleNonRDFRetrieval( URI targetURI ) {
		// TODO: Implement it
		return new ResponseEntity<Object>( HttpStatus.NOT_IMPLEMENTED );
	}

	protected ResponseEntity<Object> handleSPARQLEndpointRetrieval( URI targetURI ) {
		// TODO: Implement it
		return new ResponseEntity<Object>( HttpStatus.NOT_IMPLEMENTED );
	}
}
