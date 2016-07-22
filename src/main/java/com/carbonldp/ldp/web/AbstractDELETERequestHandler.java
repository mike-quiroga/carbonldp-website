package com.carbonldp.ldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.ldp.containers.RemoveMembersAction;
import com.carbonldp.ldp.containers.RemoveMembersActionFactory;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.models.HTTPHeader;
import com.carbonldp.models.HTTPHeaderValue;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.util.Models;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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

	public ResponseEntity<Object> handleRequest( RDFDocument requestBody, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		IRI targetIRI = getTargetIRI( request );
		if ( ! sourceService.exists( targetIRI ) ) throw new NotFoundException();

		String requestETag = getRequestETag();
		checkPrecondition( targetIRI, requestETag );

		APIPreferences.InteractionModel interactionModel = getInteractionModel( targetIRI );
		switch ( interactionModel ) {
			case RDF_SOURCE:
				return handleRDFSourceDeletion( targetIRI );
			case CONTAINER:
				return handleContainerDeletion( requestBody, targetIRI );
			case NON_RDF_SOURCE:
				return handleNonRDFDeletion( targetIRI );
			case SPARQL_ENDPOINT:
			default:
				throw new IllegalStateException();
		}
	}

	protected APIPreferences.InteractionModel getDefaultInteractionModel() {
		return APIPreferences.InteractionModel.RDF_SOURCE;
	}

	protected ResponseEntity<Object> handleRDFSourceDeletion( IRI targetIRI ) {
		delete( targetIRI );
		return createSuccessfulDeleteResponse();
	}

	protected void delete( IRI targetIRI ) {
		sourceService.delete( targetIRI );
	}

	protected ResponseEntity<Object> handleContainerDeletion( RDFDocument requestDocument, IRI targetIRI ) {
		Set<APIPreferences.ContainerDeletePreference> deletePreferences = getContainerDeletePreferences( targetIRI );

		if ( deletePreferences.contains( APIPreferences.ContainerDeletePreference.MEMBERSHIP_RESOURCES ) ) throw new NotImplementedException();
		if ( deletePreferences.contains( APIPreferences.ContainerDeletePreference.MEMBERSHIP_TRIPLES ) ) removeMembers( targetIRI );
		if ( deletePreferences.contains( APIPreferences.ContainerDeletePreference.CONTAINED_RESOURCES ) ) containerService.deleteContainedResources( targetIRI );
		if ( deletePreferences.contains( APIPreferences.ContainerDeletePreference.CONTAINER ) ) containerService.delete( targetIRI );
		if ( deletePreferences.contains( APIPreferences.ContainerDeletePreference.SELECTED_MEMBERSHIP_TRIPLES ) ) removeSelectiveMembers( requestDocument, targetIRI );

		return createSuccessfulDeleteResponse();
	}

	protected void removeMembers( IRI targetIRI ) {
		containerService.removeMembers( targetIRI );
	}

	protected void removeSelectiveMembers( RDFDocument requestDocument, IRI targetIRI ) {
		validateRequestDocument( requestDocument );

		Resource subject = Models.subject( requestDocument ).orElse( null );
		if ( subject == null ) throw new StupidityException( "The model wasn't validated like it should." );
		RemoveMembersAction members = new RemoveMembersAction( requestDocument.getBaseModel(), subject );
		validate( members );
		executeAction( targetIRI, members );
	}

	protected void executeAction( IRI targetIRI, RemoveMembersAction members ) {
		containerService.removeMembers( targetIRI, members.getMembers() );
	}

	protected void validate( RemoveMembersAction membersAction ) {
		List<Infraction> infractions = RemoveMembersActionFactory.getInstance().validate( membersAction );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	protected void validateRequestDocument( RDFDocument requestDocument ) {
		List<Infraction> infractions = new ArrayList<>();
		if ( requestDocument.subjects().size() != 1 )
			infractions.add( new Infraction( 0x2201 ) );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	protected ResponseEntity<Object> handleNonRDFDeletion( IRI targetIRI ) {
		isRDFRepresentation( targetIRI );
		sourceService.delete( targetIRI );
		return createSuccessfulDeleteResponse();
	}

	private void isRDFRepresentation( IRI targetIRI ) {
		if ( ! nonRdfSourceService.isRDFRepresentation( targetIRI ) ) throw new BadRequestException( 0x4003 );
	}

	protected ResponseEntity<Object> createSuccessfulDeleteResponse() {
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}

	@Override
	protected void checkPrecondition( IRI targetIRI, String requestETag ) {
		// TODO: Make this check a class variable (preconditionRequired = true/false)
		if ( requestETag == null ) return;
		super.checkPrecondition( targetIRI, requestETag );
	}

	protected Set<APIPreferences.ContainerDeletePreference> getContainerDeletePreferences( IRI targetIRI ) {
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
			APIPreferences.ContainerDeletePreference containerPreference = RDFNodeUtil.findByIRI( preference, APIPreferences.ContainerDeletePreference.class );
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
			APIPreferences.ContainerDeletePreference containerPreference = RDFNodeUtil.findByIRI( preference, APIPreferences.ContainerDeletePreference.class );
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
