package com.carbonldp.ldp.containers;

import com.carbonldp.descriptions.APIPreferences;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.web.AbstractRequestWithBodyHandler;
import com.carbonldp.models.EmptyResponse;
import com.carbonldp.models.Infraction;
import com.carbonldp.namespaces.C;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.ValueUtil;
import com.carbonldp.web.exceptions.BadRequestException;
import com.carbonldp.web.exceptions.NotFoundException;
import org.joda.time.DateTime;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.AbstractModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

		Set<URI> members = getMembers( requestModel );

		addMembers( targetURI, members );

		addTypeLinkHeader( APIPreferences.InteractionModel.RDF_SOURCE );
		return createSuccessfulResponse( targetURI );
	}

	protected abstract void addMembers( URI targetUri, Set<URI> members );

	protected Set<URI> getMembers( AbstractModel requestModel ) {
		return requestModel.objects()
						   .stream()
						   .map( ValueUtil::getURI )
						   .collect( Collectors.toCollection( () -> new LinkedHashSet<>() ) );
	}

	protected void validatePutRequestModel( AbstractModel requestModel ) {
		for ( Statement statement : requestModel ) {
			if ( ! ValueUtil.isBNode( statement.getSubject() ) ) throw new BadRequestException( 0x2201 );
			if ( ! statement.getPredicate().stringValue().equals( C.Properties.ADD_MEMBER ) ) throw new BadRequestException( 0x2202 );
			if ( ! ValueUtil.isURI( statement.getObject() ) ) throw new InvalidResourceException( new Infraction( 0x2005, "property", C.Properties.ADD_MEMBER ) );
		}
	}

	protected ResponseEntity<Object> createSuccessfulResponse( URI affectedResourceURI ) {
		DateTime modified = sourceService.getModified( affectedResourceURI );

		setETagHeader( modified );
		return new ResponseEntity<>( new EmptyResponse(), HttpStatus.OK );
	}
}
