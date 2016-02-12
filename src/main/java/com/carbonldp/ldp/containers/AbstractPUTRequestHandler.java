package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.web.AbstractRequestWithBodyHandler;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.exceptions.NotFoundException;
import org.joda.time.DateTime;
import org.openrdf.model.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MiguelAraCo
 * @since 0.10.0-ALPHA
 */
public abstract class AbstractPUTRequestHandler<E extends RDFResource> extends AbstractRequestWithBodyHandler<E> {

	public ResponseEntity<Object> handleRequest( RDFDocument requestDocument, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );

		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) {
			throw new NotFoundException();
		}

		String requestETag = getRequestETag();
		checkPrecondition( targetURI, requestETag );

		validateRequest( requestDocument );
		AddMembersAction membersAction = new AddMembersAction( requestDocument.getBaseModel(), requestDocument.subjectResource() );
		executeAction( targetURI, membersAction );

		addTypeLinkHeader( APIPreferences.InteractionModel.RDF_SOURCE );
		return createSuccessfulResponse( targetURI );
	}

	protected abstract void executeAction( URI targetUri, AddMembersAction members );

	protected void validateRequest( RDFDocument requestDocument ) {
		List<Infraction> infractions = new ArrayList<>();
		if ( requestDocument.subjects().size() != 1 )
			infractions.add( new Infraction( 0x2201 ) );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	protected ResponseEntity<Object> createSuccessfulResponse( URI affectedResourceURI ) {
		DateTime modified = sourceService.getModified( affectedResourceURI );

		setETagHeader( modified );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}
}
