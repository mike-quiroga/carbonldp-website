package com.carbonldp.ldp.web;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.namespaces.C;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.joda.time.DateTime;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.AbstractModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

@Transactional
public abstract class AbstractRDFPutRequestHandler<E extends RDFResource> extends AbstractRequestWithBodyHandler<E> {

	public AbstractRDFPutRequestHandler() {
		Set<APIPreferences.InteractionModel> supportedInteractionModels = new HashSet<>();
		supportedInteractionModels.add( APIPreferences.InteractionModel.RDF_SOURCE );
		supportedInteractionModels.add( APIPreferences.InteractionModel.CONTAINER );
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

		APIPreferences.InteractionModel interactionModel = getInteractionModel( targetURI );
		switch ( interactionModel ) {
			case RDF_SOURCE:
				return handlePUTToRDFSource( targetURI, requestModel );
			case CONTAINER:
				return handlePUTToContainer( targetURI, requestModel );
			default:
				throw new BadRequestException( "The interaction model provided isn't supported in PUT requests." );
		}
	}

	@Override
	protected void validateDocumentResource( URI targetURI, RDFResource requestDocumentResource ) {
		super.validateDocumentResource( targetURI, requestDocumentResource );
		if ( ! targetURI.equals( requestDocumentResource.getURI() ) ) throw new BadRequestException( "The documentResource's URI, sent in the request, is different to the request URI. Remember POST to parent, PUT to me." );
	}

	protected abstract E getDocumentResourceView( RDFResource requestDocumentResource );

	protected ResponseEntity<Object> handlePUTToContainer( URI targetURI, AbstractModel requestModel ) {
		validateAddMemberModel( requestModel );

		Set<URI> members = getMembersList( requestModel );

		containerService.addMembers( targetURI, members );

		return createSuccessfulResponse( targetURI );
	}

	protected Set<URI> getMembersList( AbstractModel requestModel ) {
		Set<Value> objects = requestModel.objects();
		Iterator<Value> iterator = objects.iterator();
		Set<URI> members = new LinkedHashSet<>( );

		while ( iterator.hasNext() ) {
			members.add((URI) iterator.next());
		}
		return members;
	}

	protected void validateAddMemberModel( AbstractModel requestModel ) {
		if ( requestModel.size() == 0 ) throw new BadRequestException( "Body cannot be empty" );
		Set<Resource> subjects = requestModel.subjects();
		Set<URI> predicates = requestModel.predicates();
		Set<Value> objects = requestModel.objects();

		Iterator iterator = subjects.iterator();
		while ( iterator.hasNext() ) {
			Object subject = iterator.next();
			if ( subject instanceof URI ) {
				URI subjectURI = (URI) subject;
				if ( ! subjectURI.stringValue().equals( C.NAMESPACE + "action" ) )
					throw new BadRequestException( "Subject URI not supported" );
			} else {
				throw new BadRequestException( "Subject is not a URI" );
			}
		}

		iterator = predicates.iterator();
		while ( iterator.hasNext() ) {
			URI subjectURI = (URI) iterator.next();
			if ( ! subjectURI.stringValue().equals( C.Properties.ADD_MEMBER ) )
				throw new BadRequestException( "predicate not supported" );
		}

		iterator = objects.iterator();
		while ( iterator.hasNext() ) {
			Value object = (Value) iterator.next();
			if ( ! ValueUtil.isURI( object ) ) throw new BadRequestException( "triplet's object is nor a URI" );
		}

	}

	protected RDFResource convertToRDFResource( AbstractModel requestModel ) {
		validateRequestModel( requestModel );
		RDFResource requestDocumentResource = getRequestDocumentResource( requestModel );

		return requestDocumentResource;
	}

	protected ResponseEntity<Object> handlePUTToRDFSource( URI targetURI, AbstractModel requestModel ) {
		RDFResource requestDocumentResource = convertToRDFResource( requestModel );
		seekForOrphanFragments( requestModel, requestDocumentResource );

		validateDocumentResource( targetURI, requestDocumentResource );
		E documentResourceView = getDocumentResourceView( requestDocumentResource );
		validateDocumentResourceView( documentResourceView );

		replaceResource( targetURI, documentResourceView );

		addTypeLinkHeader( APIPreferences.InteractionModel.RDF_SOURCE );
		return createSuccessfulResponse( targetURI );
	}

	protected ResponseEntity<Object> createSuccessfulResponse( URI affectedResourceURI ) {
		DateTime modified = sourceService.getModified( affectedResourceURI );

		setETagHeader( modified );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}

	protected abstract void replaceResource( URI targetURI, E documentResourceView );

}
