package com.carbonldp.ldp.web;

import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.IRI;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author MiguelAraCo
 * @since _version_
 */
@RequestHandler
public class AbstractSPARQLUpdatePOSTRequestHandler extends AbstractLDPRequestHandler {
	public ResponseEntity<Object> handleRequest( String queryString, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );
		IRI targetIRI = getTargetIRI( request );
		if ( ! targetResourceExists( targetIRI ) ) {
			throw new NotFoundException();
		}
		sparqlService.executeSPARQLUpdate( queryString, targetIRI );
		return null;
	}
}
