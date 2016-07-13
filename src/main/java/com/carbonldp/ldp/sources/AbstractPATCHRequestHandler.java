package com.carbonldp.ldp.sources;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.patch.*;
import com.carbonldp.ldp.web.AbstractLDPRequestHandler;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.models.Infraction;
import com.carbonldp.namespaces.CP;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFNode;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.impl.AbstractModel;
import org.eclipse.rdf4j.model.impl.LinkedHashModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public abstract class AbstractPATCHRequestHandler extends AbstractLDPRequestHandler {

	public AbstractPATCHRequestHandler() {
		Set<APIPreferences.InteractionModel> supportedInteractionModels = new HashSet<>();
		supportedInteractionModels.add( APIPreferences.InteractionModel.RDF_SOURCE );
		setSupportedInteractionModels( supportedInteractionModels );

		setDefaultInteractionModel( APIPreferences.InteractionModel.RDF_SOURCE );
	}

	public ResponseEntity<Object> handleRequest( AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		IRI targetIRI = getTargetIRI( request );
		if ( ! targetResourceExists( targetIRI ) ) throw new NotFoundException();

		String requestETag = getRequestETag();
		checkPrecondition( targetIRI, requestETag );

		// TODO: Validate subjects

		PATCHRequest patchRequest = getPATCHRequest( requestModel );

		// validateDocumentResourceView( patchRequest );
		// validateAdditionalResources( requestModel, patchRequest, targetIRI );

		Set<DeleteAction> deleteActions = getDeleteActions( patchRequest );
		executeDeleteActions( targetIRI, deleteActions );

		Set<SetAction> setActions = getSetActions( patchRequest );
		executeSetActions( targetIRI, setActions );

		Set<AddAction> addActions = getAddActions( patchRequest );
		executeAddActions( targetIRI, addActions );

		String eTag = sourceService.getETag( targetIRI );
		setStrongETagHeader( eTag );

		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}

	/*
	protected void validateDocumentResourceView( PATCHRequest patchRequest ) {
		if ( patchRequest == null ) throw new BadRequestException( "The request doesn't contain a cp:PATCHRequest object." );
		List<Infraction> infractions = PATCHRequestFactory.validateBasicContainer( patchRequest );
		if ( ! infractions.isEmpty() ) throw new BadRequestException( "The cp:PATCHRequest provided isn't valid." );
	}

	private void validateAdditionalResources( AbstractModel requestModel, PATCHRequest patchRequest, IRI targetIRI ) {
		requestModel.subjects()
					.stream()
					.filter( ValueUtil::isIRI )
					.map( ValueUtil::getIRI )
					.filter( uri -> ! uri.equals( patchRequest.getIRI() ) )
					.forEach( uri -> {
						if ( ! belongsToRequestDomain( targetIRI, uri ) ) throw new BadRequestException( "The resource is outside of the request's domain." );
					} )
		;
	}


	private boolean belongsToRequestDomain( IRI targetIRI, IRI uri ) {
		return IRIUtil.isImmediateChild( targetIRI.stringValue(), uri.stringValue() );
	}
	*/

	private PATCHRequest getPATCHRequest( AbstractModel requestModel ) {
		PATCHRequest patchRequest = null;
		for ( Resource subject : requestModel.subjects() ) {
			RDFNode node = new RDFNode( requestModel, subject );
			if ( ! node.hasType( PATCHRequestDescription.Resource.CLASS ) ) continue;

			if ( patchRequest != null ) throw new BadRequestException( 0x2301 );

			patchRequest = new PATCHRequest( node );
		}

		if ( patchRequest == null ) throw new InvalidResourceException( new Infraction( 0x2003, "property", CP.Classes.PATCH_REQUEST ) );

		return patchRequest;
	}

	private Set<DeleteAction> getDeleteActions( PATCHRequest patchRequest ) {
		return patchRequest.getDeleteActions()
				.stream()
				.map( uri -> new RDFNode( patchRequest.getBaseModel(), uri ) )
				.map( DeleteAction::new )
				.collect( Collectors.toSet() )
			;
	}

	//TODO: to implement to accept bnodes and fragments
	private void executeDeleteActions( IRI sourceIRI, Set<DeleteAction> actions ) {
		Set<RDFResource> resourcesToDelete = new HashSet<>();
		RDFDocument document = new RDFDocument( new LinkedHashModel(), sourceIRI );
		for ( DeleteAction action : actions ) {
			for ( Statement actionStatement : action ) {
				DeleteActionDescription.Property actionSpecialProperty = getDeleteActionSpecialProperty( actionStatement );
				if ( actionSpecialProperty != null ) executeDeleteActionSpecialProperty( sourceIRI, action, actionSpecialProperty );
				else document.add( sourceIRI, actionStatement.getPredicate(), actionStatement.getObject() );
			}
		}

		deleteResourceViews( sourceIRI, document );
	}

	protected void deleteResourceViews( IRI sourceIRI, RDFDocument document ) {
		sourceService.subtract( sourceIRI, document );
	}

	private DeleteActionDescription.Property getDeleteActionSpecialProperty( Statement actionStatement ) {
		IRI predicate = actionStatement.getPredicate();
		return RDFNodeUtil.findByIRI( predicate, DeleteActionDescription.Property.class );
	}

	private void executeDeleteActionSpecialProperty( IRI sourceIRI, DeleteAction action, DeleteActionDescription.Property actionSpecialProperty ) {
		switch ( actionSpecialProperty ) {
			default:
				throw new RuntimeException( "Not Implemented" );
		}
	}

	private Set<SetAction> getSetActions( PATCHRequest patchRequest ) {
		return patchRequest.getSetActions()
				.stream()
				.map( uri -> new RDFNode( patchRequest.getBaseModel(), uri ) )
				.map( SetAction::new )
				.collect( Collectors.toSet() )
			;
	}

	//TODO: to implement to accept bnodes and fragments
	private void executeSetActions( IRI sourceIRI, Set<SetAction> actions ) {
		RDFDocument document = new RDFDocument( new LinkedHashModel(), sourceIRI );

		for ( SetAction action : actions ) {
			for ( Statement actionStatement : action ) {
				SetActionDescription.Property actionSpecialProperty = getSetActionSpecialProperty( actionStatement );
				if ( actionSpecialProperty != null ) executeSetActionSpecialProperty( sourceIRI, action, actionSpecialProperty );
				else document.add( sourceIRI, actionStatement.getPredicate(), actionStatement.getObject() );
			}
		}

		setResourceViews( sourceIRI, document );
	}

	protected void setResourceViews( IRI sourceIRI, RDFDocument document ) {
		sourceService.set( sourceIRI, document );
	}

	private SetActionDescription.Property getSetActionSpecialProperty( Statement actionStatement ) {
		IRI predicate = actionStatement.getPredicate();
		return RDFNodeUtil.findByIRI( predicate, SetActionDescription.Property.class );
	}

	private void executeSetActionSpecialProperty( IRI sourceIRI, SetAction action, SetActionDescription.Property actionSpecialProperty ) {
		switch ( actionSpecialProperty ) {
			default:
				throw new RuntimeException( "Not Implemented" );
		}
	}

	private Set<AddAction> getAddActions( PATCHRequest patchRequest ) {
		return patchRequest.getAddActions()
				.stream()
				.map( uri -> new RDFNode( patchRequest.getBaseModel(), uri ) )
				.map( AddAction::new )
				.collect( Collectors.toSet() )
			;
	}

	//TODO: to implement to accept bnodes and fragments
	private void executeAddActions( IRI sourceIRI, Collection<AddAction> actions ) {
		RDFDocument document = new RDFDocument( new LinkedHashModel(), sourceIRI );
		for ( AddAction action : actions ) {
			for ( Statement actionStatement : action ) {
				AddActionDescription.Property actionSpecialProperty = getAddActionSpecialProperty( actionStatement );
				if ( actionSpecialProperty != null ) executeAddActionSpecialProperty( sourceIRI, action, actionSpecialProperty );
				else document.add( sourceIRI, actionStatement.getPredicate(), actionStatement.getObject() );
			}
		}

		addResourceViews( sourceIRI, document );
	}

	protected void addResourceViews( IRI sourceIRI, RDFDocument document ) {
		sourceService.add( sourceIRI, document );
	}

	private AddActionDescription.Property getAddActionSpecialProperty( Statement actionStatement ) {
		IRI predicate = actionStatement.getPredicate();
		return RDFNodeUtil.findByIRI( predicate, AddActionDescription.Property.class );
	}

	private void executeAddActionSpecialProperty( IRI sourceIRI, AddAction action, AddActionDescription.Property actionSpecialProperty ) {
		switch ( actionSpecialProperty ) {
			default:
				throw new RuntimeException( "Not Implemented" );
		}
	}
}
