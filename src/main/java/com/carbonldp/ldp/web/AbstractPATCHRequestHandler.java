package com.carbonldp.ldp.web;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.ldp.patch.*;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.URIUtil;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Transactional
public abstract class AbstractPATCHRequestHandler<T extends PATCHRequest> extends AbstractRequestWithBodyHandler<T> {

	public AbstractPATCHRequestHandler() {
		Set<APIPreferences.InteractionModel> supportedInteractionModels = new HashSet<>();
		supportedInteractionModels.add( APIPreferences.InteractionModel.RDF_SOURCE );
		setSupportedInteractionModels( supportedInteractionModels );

		setDefaultInteractionModel( APIPreferences.InteractionModel.RDF_SOURCE );
	}

	public ResponseEntity<Object> handleRequest( AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) {
			throw new NotFoundException( "The target resource wasn't found." );
		}

		String requestETag = getRequestETag();
		checkPrecondition( targetURI, requestETag );
		validateRequestModel( requestModel );

		PATCHRequest patchRequest = getPATCHRequest( requestModel );

		validateDocumentResourceView( patchRequest );
		validateAdditionalResources( requestModel, patchRequest, targetURI );

		APIPreferences.InteractionModel interactionModel = getInteractionModel( targetURI );
		switch ( interactionModel ) {
			case RDF_SOURCE:
				return handlePATCHToRDFSource( targetURI, patchRequest );
			default:
				throw new BadRequestException( "The interaction model provided isn't supported in PATCH requests." );
		}
	}

	@Override
	protected void validateDocumentResourceView( PATCHRequest patchRequest ) {
		if ( patchRequest == null ) throw new BadRequestException( "The request doesn't contain a cp:PATCHRequest object." );
		List<Infraction> infractions = PATCHRequestFactory.validate( patchRequest );
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

	protected ResponseEntity<Object> handlePATCHToRDFSource( URI targetURI, PATCHRequest patchRequest ) {
		Set<DeleteAction> deleteActions = getDeleteActions( patchRequest );
		executeDeleteActions( targetURI, deleteActions );

		Set<SetAction> setActions = getSetActions( patchRequest );
		executeSetActions( targetURI, setActions );

		Set<AddAction> addActions = getAddActions( patchRequest );
		executeAddActions( targetURI, addActions );

		DateTime eTag = sourceService.getModified( targetURI );
		setETagHeader( eTag );

		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}

	private PATCHRequest getPATCHRequest( AbstractModel requestModel ) {
		Set<RDFResource> documentResources = getRequestDocumentResources( requestModel );

		PATCHRequest patchRequest = null;
		for ( RDFResource documentResource : documentResources ) {
			if ( hasGenericRequestURI( documentResource ) ) {
				if ( PATCHRequestFactory.is( documentResource ) ) {
					if ( patchRequest != null ) throw new BadRequestException( "The request contains multiple cp:PATCHRequest." );
					patchRequest = new PATCHRequest( documentResource );
				} else throw new BadRequestException( "A resource with a generic URI isn't a cp:PATCHRequest." );
			}
		}
		return patchRequest;
	}

	private Set<DeleteAction> getDeleteActions( PATCHRequest patchRequest ) {
		return patchRequest.getDeleteActions()
						   .stream()
						   .map( uri -> new RDFResource( patchRequest.getBaseModel(), uri ) )
						   .map( DeleteAction::new )
						   .collect( Collectors.toSet() )
			;
	}

	private void executeDeleteActions( URI sourceURI, Set<DeleteAction> actions ) {
		Set<RDFResource> resourcesToDelete = new HashSet<>();
		for ( DeleteAction action : actions ) {
			RDFResource resourceToDelete = new RDFResource( action.getSubjectURI() );
			for ( Statement actionStatement : action ) {
				DeleteActionDescription.Property actionSpecialProperty = getDeleteActionSpecialProperty( actionStatement );
				if ( actionSpecialProperty != null ) executeDeleteActionSpecialProperty( sourceURI, action, actionSpecialProperty );
				else resourceToDelete.add( actionStatement.getPredicate(), actionStatement.getObject() );
			}
			resourcesToDelete.add( resourceToDelete );
		}

		deleteResourceViews( sourceURI, resourcesToDelete );
	}

	protected void deleteResourceViews( URI sourceURI, Set<RDFResource> resourceViews ) {
		sourceService.substract( sourceURI, resourceViews );
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
						   .map( uri -> new RDFResource( patchRequest.getBaseModel(), uri ) )
						   .map( SetAction::new )
						   .collect( Collectors.toSet() )
			;
	}

	private void executeSetActions( URI sourceURI, Set<SetAction> actions ) {
		Set<RDFResource> resourceViews = new HashSet<>();

		for ( SetAction action : actions ) {
			RDFResource resourceView = new RDFResource( action.getSubjectURI() );
			for ( Statement actionStatement : action ) {
				SetActionDescription.Property actionSpecialProperty = getSetActionSpecialProperty( actionStatement );
				if ( actionSpecialProperty != null ) executeSetActionSpecialProperty( sourceURI, action, actionSpecialProperty );
				else resourceView.add( actionStatement.getPredicate(), actionStatement.getObject() );
			}
			if ( ! resourceView.isEmpty() ) resourceViews.add( resourceView );
		}

		setResourceViews( sourceURI, resourceViews );
	}

	protected void setResourceViews( URI sourceURI, Set<RDFResource> resourceViews ) {
		sourceService.set( sourceURI, resourceViews );
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
						   .map( uri -> new RDFResource( patchRequest.getBaseModel(), uri ) )
						   .map( AddAction::new )
						   .collect( Collectors.toSet() )
			;
	}

	private void executeAddActions( URI sourceURI, Collection<AddAction> actions ) {
		Set<RDFResource> resourceViews = new HashSet<>();
		for ( AddAction action : actions ) {
			RDFResource resourceView = new RDFResource( action.getSubjectURI() );
			for ( Statement actionStatement : action ) {
				AddActionDescription.Property actionSpecialProperty = getAddActionSpecialProperty( actionStatement );
				if ( actionSpecialProperty != null ) executeAddActionSpecialProperty( sourceURI, action, actionSpecialProperty );
				else resourceView.add( actionStatement.getPredicate(), actionStatement.getObject() );
			}
			resourceViews.add( resourceView );
		}

		addResourceViews( sourceURI, resourceViews );
	}

	protected void addResourceViews( URI sourceURI, Set<RDFResource> resourceViews ) {
		sourceService.add( sourceURI, resourceViews );
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
