package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.ldp.web.AbstractRequestWithBodyHandler;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFDocument;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.IRI;
import org.openrdf.model.Resource;
import org.openrdf.model.util.Models;
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

		IRI targetIRI = getTargetIRI( request );
		if ( ! targetResourceExists( targetIRI ) ) {
			throw new NotFoundException();
		}

		validateRequest( requestDocument );
		Resource subject = Models.subject( requestDocument ).orElse( null );
		if ( subject == null ) throw new StupidityException( "The model wasn't validated like it should" );
		AddMembersAction membersAction = new AddMembersAction( requestDocument.getBaseModel(), subject );
		executeAction( targetIRI, membersAction );

		addTypeLinkHeader( APIPreferences.InteractionModel.RDF_SOURCE );
		return createSuccessfulResponse( targetIRI );
	}

	protected abstract void executeAction( IRI targetUri, AddMembersAction members );

	protected void validateRequest( RDFDocument requestDocument ) {
		List<Infraction> infractions = new ArrayList<>();
		if ( requestDocument.subjects().size() != 1 )
			infractions.add( new Infraction( 0x2201 ) );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	protected ResponseEntity<Object> createSuccessfulResponse( IRI affectedResourceIRI ) {
		String eTag = sourceService.getETag( affectedResourceIRI );

		setStrongETagHeader( eTag );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}
}
