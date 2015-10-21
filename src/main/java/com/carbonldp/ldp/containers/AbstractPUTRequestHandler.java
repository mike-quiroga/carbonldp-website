package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.web.AbstractRequestWithBodyHandler;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.exceptions.NotFoundException;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author MiguelAraCo
 * @since 0.10.0-ALPHA
 */
public abstract class AbstractPUTRequestHandler<E extends RDFResource> extends AbstractRequestWithBodyHandler<E> {

	public ResponseEntity<Object> handleRequest( AbstractModel requestModel, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) {
			throw new NotFoundException();
		}

		String requestETag = getRequestETag();
		checkPrecondition( targetURI, requestETag );

		validatePutRequestModel( requestModel );
		AddMembersAction membersToAdd = getMembersToAdd( requestModel );
		addMembers( targetURI, membersToAdd );

		addTypeLinkHeader( APIPreferences.InteractionModel.RDF_SOURCE );
		return createSuccessfulResponse( targetURI );
	}

	protected abstract void addMembers( URI targetUri, AddMembersAction members );

	protected AddMembersAction getMembersToAdd( AbstractModel requestModel ) {
		return AddMembersActionFactory.getInstance().create( requestModel );
	}

	protected void validatePutRequestModel( AbstractModel requestModel ) {
		List<Infraction> infractions = AddMembersActionFactory.getInstance().validate( requestModel );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	protected ResponseEntity<Object> createSuccessfulResponse( URI affectedResourceURI ) {
		DateTime modified = sourceService.getModified( affectedResourceURI );

		setETagHeader( modified );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}
}
