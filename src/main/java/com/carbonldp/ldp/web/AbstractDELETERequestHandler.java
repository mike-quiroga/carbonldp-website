package com.carbonldp.ldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.containers.RemoveMembersAction;
import com.carbonldp.ldp.containers.RemoveMembersActionFactory;
import com.carbonldp.ldp.nonrdf.RDFRepresentation;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.models.HTTPHeader;
import com.carbonldp.models.HTTPHeaderValue;
import com.carbonldp.models.Infraction;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
public class AbstractDELETERequestHandler extends AbstractLDPRequestHandler {

	private static Set<APIPreferences.ContainerDeletePreference> DEFAULT_CDP;

	static {
		Set<APIPreferences.ContainerDeletePreference> tempCDP = new HashSet<>();
		tempCDP.add( APIPreferences.ContainerDeletePreference.MEMBERSHIP_TRIPLES );
		DEFAULT_CDP = Collections.unmodifiableSet( tempCDP );
	}

	public AbstractDELETERequestHandler() {
		Set<APIPreferences.InteractionModel> supportedInteractionModels = new HashSet<>();
		supportedInteractionModels.add( APIPreferences.InteractionModel.RDF_SOURCE );
		supportedInteractionModels.add( APIPreferences.InteractionModel.CONTAINER );
		supportedInteractionModels.add( APIPreferences.InteractionModel.NON_RDF_SOURCE );
		setSupportedInteractionModels( supportedInteractionModels );

		setDefaultInteractionModel( APIPreferences.InteractionModel.CONTAINER );
	}

	public ResponseEntity<Object> handleRequest( AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		URI targetURI = getTargetURI( request );
		if ( ! sourceService.exists( targetURI ) ) throw new NotFoundException();

		String requestETag = getRequestETag();
		checkPrecondition( targetURI, requestETag );

		APIPreferences.InteractionModel interactionModel = getInteractionModel( targetURI );
		switch ( interactionModel ) {
			case RDF_SOURCE:
				return handleRDFSourceDeletion( targetURI );
			case CONTAINER:
				return handleContainerDeletion( requestModel, targetURI );
			case NON_RDF_SOURCE:
				return handleNonRDFDeletion( targetURI );
			case SPARQL_ENDPOINT:
			default:
				throw new IllegalStateException();
		}
	}

	protected ResponseEntity<Object> handleRDFSourceDeletion( URI targetURI ) {
		delete( targetURI );
		return createSuccessfulDeleteResponse();
	}

	protected void delete( URI targetURI ) {
		sourceService.delete( targetURI );
	}

	protected ResponseEntity<Object> handleContainerDeletion( AbstractModel requestModel, URI targetURI ) {
		Set<APIPreferences.ContainerDeletePreference> deletePreferences = getContainerDeletePreferences( targetURI );

		if ( deletePreferences.contains( APIPreferences.ContainerDeletePreference.MEMBERSHIP_RESOURCES ) ) throw new NotImplementedException();
		if ( deletePreferences.contains( APIPreferences.ContainerDeletePreference.MEMBERSHIP_TRIPLES ) ) containerService.removeMembers( targetURI );
		if ( deletePreferences.contains( APIPreferences.ContainerDeletePreference.CONTAINED_RESOURCES ) ) containerService.deleteContainedResources( targetURI );
		if ( deletePreferences.contains( APIPreferences.ContainerDeletePreference.CONTAINER ) ) containerService.delete( targetURI );
		if ( deletePreferences.contains( APIPreferences.ContainerDeletePreference.SELECTED_MEMBERSHIP_TRIPLES ) ) removeSelectiveMembers( requestModel, targetURI );

		return createSuccessfulDeleteResponse();
	}

	protected void removeSelectiveMembers( AbstractModel requestModel, URI targetURI ) {
		validateDeleteRequestModel( requestModel );
		RemoveMembersAction membersToRemove = getMembersToRemove( requestModel );
		containerService.removeMembers( targetURI, membersToRemove );
	}

	protected RemoveMembersAction getMembersToRemove( AbstractModel requestModel ) {
		return RemoveMembersActionFactory.getInstance().create( requestModel );
	}

	protected void validateDeleteRequestModel( AbstractModel requestModel ) {
		List<Infraction> infractions = RemoveMembersActionFactory.getInstance().validate( requestModel );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	protected ResponseEntity<Object> handleNonRDFDeletion( URI targetURI ) {
		isRDFRepresentation( targetURI );
		RDFRepresentation rdfRepresentation = new RDFRepresentation( sourceService.get( targetURI ) );
		nonRdfSourceService.deleteResource( rdfRepresentation );
		sourceService.delete( targetURI );

		return createSuccessfulDeleteResponse();
	}

	private void isRDFRepresentation( URI targetURI ) {
		if ( ! nonRdfSourceService.isRDFRepresentation( targetURI ) ) throw new BadRequestException( 0x4003 );
	}

	protected ResponseEntity<Object> createSuccessfulDeleteResponse() {
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}

	@Override
	protected void checkPrecondition( URI targetURI, String requestETag ) {
		// TODO: Make this check a class variable (preconditionRequired = true/false)
		if ( requestETag == null ) return;
		super.checkPrecondition( targetURI, requestETag );
	}

	protected Set<APIPreferences.ContainerDeletePreference> getContainerDeletePreferences( URI targetURI ) {
		Set<APIPreferences.ContainerDeletePreference> preferences = new HashSet<>();
		Set<APIPreferences.ContainerDeletePreference> defaultPreferences = getDefaultContainerDeletePreferences();

		// Container specific preferences could be added here

		preferences.addAll( defaultPreferences );

		return getContainerDeletePreferences( preferences, request );
	}

	private Set<APIPreferences.ContainerDeletePreference> getContainerDeletePreferences( Set<APIPreferences.ContainerDeletePreference> defaultPreferences, HttpServletRequest request ) {
		HTTPHeader preferHeader = new HTTPHeader( request.getHeaders( HTTPHeaders.PREFER ) );
		List<HTTPHeaderValue> includePreferences = HTTPHeader.filterHeaderValues( preferHeader, "include", null, null, null );
		List<HTTPHeaderValue> omitPreferences = HTTPHeader.filterHeaderValues( preferHeader, "omit", null, null, null );

		Set<APIPreferences.ContainerDeletePreference> appliedPreferences = new HashSet<>();

		for ( HTTPHeaderValue omitPreference : omitPreferences ) {
			//TODO  LDP-377
			String preference = omitPreference.getMainValue();
			if ( preference == null || preference.isEmpty() )
				continue;
			APIPreferences.ContainerDeletePreference containerPreference = RDFNodeUtil.findByURI( preference, APIPreferences.ContainerDeletePreference.class );
			if ( containerPreference == null ) continue;

			appliedPreferences.add( containerPreference );
			// TODO: Add AppliedPreference Header
			if ( defaultPreferences.contains( containerPreference ) ) defaultPreferences.remove( containerPreference );
		}

		for ( HTTPHeaderValue includePreference : includePreferences ) {
			//TODO  LDP-377
			String preference = includePreference.getMainValue();
			if ( preference == null || preference.isEmpty() )
				continue;
			if ( includePreference.getMain() == null || includePreference.getMain().isEmpty() )
				continue;
			APIPreferences.ContainerDeletePreference containerPreference = RDFNodeUtil.findByURI( preference, APIPreferences.ContainerDeletePreference.class );
			if ( containerPreference == null ) continue;

			// TODO: Add AppliedPreference Header
			if ( appliedPreferences.contains( containerPreference ) ) throw new BadRequestException( 0x5001 );
			if ( ! defaultPreferences.contains( containerPreference ) ) defaultPreferences.add( containerPreference );
		}

		return defaultPreferences;
	}

	protected Set<APIPreferences.ContainerDeletePreference> getDefaultContainerDeletePreferences() {
		return DEFAULT_CDP;
	}
}
