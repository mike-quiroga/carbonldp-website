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
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.openrdf.model.impl.LinkedHashModel;
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

		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) throw new NotFoundException();

		String requestETag = getRequestETag();
		checkPrecondition( targetURI, requestETag );

		// TODO: Validate subjects

		PATCHRequest patchRequest = getPATCHRequest( requestModel );

		// validateDocumentResourceView( patchRequest );
		// validateAdditionalResources( requestModel, patchRequest, targetURI );

		Set<DeleteAction> deleteActions = getDeleteActions( patchRequest );
		executeDeleteActions( targetURI, deleteActions );

		Set<SetAction> setActions = getSetActions( patchRequest );
		executeSetActions( targetURI, setActions );

		Set<AddAction> addActions = getAddActions( patchRequest );
		executeAddActions( targetURI, addActions );

		int eTag = sourceService.getETag( targetURI );
		setStrongETagHeader( eTag );

		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}

	/*
	protected void validateDocumentResourceView( PATCHRequest patchRequest ) {
		if ( patchRequest == null ) throw new BadRequestException( "The request doesn't contain a cp:PATCHRequest object." );
		List<Infraction> infractions = PATCHRequestFactory.validateBasicContainer( patchRequest );
		if ( ! infractions.isEmpty() ) throw new BadRequestException( "The cp:PATCHRequest provided isn't valid." );
	}

	private void validateAdditionalResources( AbstractModel requestModel, PATCHRequest patchRequest, URI targetURI ) {
		requestModel.subjects()
					.stream()
					.filter( ValueUtil::isURI )
					.map( ValueUtil::getURI )
					.filter( uri -> ! uri.equals( patchRequest.getURI() ) )
					.forEach( uri -> {
						if ( ! belongsToRequestDomain( targetURI, uri ) ) throw new BadRequestException( "The resource is outside of the request's domain." );
					} )
		;
	}


	private boolean belongsToRequestDomain( URI targetURI, URI uri ) {
		return URIUtil.isImmediateChild( targetURI.stringValue(), uri.stringValue() );
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
	private void executeDeleteActions( URI sourceURI, Set<DeleteAction> actions ) {
		Set<RDFResource> resourcesToDelete = new HashSet<>();
		RDFDocument document = new RDFDocument( new LinkedHashModel(), sourceURI );
		for ( DeleteAction action : actions ) {
			for ( Statement actionStatement : action ) {
				DeleteActionDescription.Property actionSpecialProperty = getDeleteActionSpecialProperty( actionStatement );
				if ( actionSpecialProperty != null ) executeDeleteActionSpecialProperty( sourceURI, action, actionSpecialProperty );
				else document.add( sourceURI, actionStatement.getPredicate(), actionStatement.getObject() );
			}
		}

		deleteResourceViews( sourceURI, document );
	}

	protected void deleteResourceViews( URI sourceURI, RDFDocument document ) {
		sourceService.subtract( sourceURI, document );
	}

	private DeleteActionDescription.Property getDeleteActionSpecialProperty( Statement actionStatement ) {
		URI predicate = actionStatement.getPredicate();
		return RDFNodeUtil.findByURI( predicate, DeleteActionDescription.Property.class );
	}

	private void executeDeleteActionSpecialProperty( URI sourceURI, DeleteAction action, DeleteActionDescription.Property actionSpecialProperty ) {
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
	private void executeSetActions( URI sourceURI, Set<SetAction> actions ) {
		RDFDocument document = new RDFDocument( new LinkedHashModel(), sourceURI );

		for ( SetAction action : actions ) {
			for ( Statement actionStatement : action ) {
				SetActionDescription.Property actionSpecialProperty = getSetActionSpecialProperty( actionStatement );
				if ( actionSpecialProperty != null ) executeSetActionSpecialProperty( sourceURI, action, actionSpecialProperty );
				else document.add( sourceURI, actionStatement.getPredicate(), actionStatement.getObject() );
			}
		}

		setResourceViews( sourceURI, document );
	}

	protected void setResourceViews( URI sourceURI, RDFDocument document ) {
		sourceService.set( sourceURI, document );
	}

	private SetActionDescription.Property getSetActionSpecialProperty( Statement actionStatement ) {
		URI predicate = actionStatement.getPredicate();
		return RDFNodeUtil.findByURI( predicate, SetActionDescription.Property.class );
	}

	private void executeSetActionSpecialProperty( URI sourceURI, SetAction action, SetActionDescription.Property actionSpecialProperty ) {
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
	private void executeAddActions( URI sourceURI, Collection<AddAction> actions ) {
		RDFDocument document = new RDFDocument( new LinkedHashModel(), sourceURI );
		for ( AddAction action : actions ) {
			for ( Statement actionStatement : action ) {
				AddActionDescription.Property actionSpecialProperty = getAddActionSpecialProperty( actionStatement );
				if ( actionSpecialProperty != null ) executeAddActionSpecialProperty( sourceURI, action, actionSpecialProperty );
				else document.add( sourceURI, actionStatement.getPredicate(), actionStatement.getObject() );
			}
		}

		addResourceViews( sourceURI, document );
	}

	protected void addResourceViews( URI sourceURI, RDFDocument document ) {
		sourceService.add( sourceURI, document );
	}

	private AddActionDescription.Property getAddActionSpecialProperty( Statement actionStatement ) {
		URI predicate = actionStatement.getPredicate();
		return RDFNodeUtil.findByURI( predicate, AddActionDescription.Property.class );
	}

	private void executeAddActionSpecialProperty( URI sourceURI, AddAction action, AddActionDescription.Property actionSpecialProperty ) {
		switch ( actionSpecialProperty ) {
			default:
				throw new RuntimeException( "Not Implemented" );
		}
	}
}
