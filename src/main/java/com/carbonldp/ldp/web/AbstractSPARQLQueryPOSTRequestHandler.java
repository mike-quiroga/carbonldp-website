package com.carbonldp.ldp.web;

import com.carbonldp.sparql.SPARQLResult;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestHandler
public class AbstractSPARQLQueryPOSTRequestHandler extends AbstractLDPRequestHandler {
	public ResponseEntity<Object> handleRequest( String queryString, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );
		if ( request.getHeader( "default-graph-uri" ) != null || request.getHeader( "default-graph-uri" ) != null )
			return new ResponseEntity<>( HttpStatus.BAD_REQUEST );
		URI targetURI = getTargetURI( request );
		if ( ! targetResourceExists( targetURI ) ) {
			throw new NotFoundException( "The target resource wasn't found." );
		}

		SPARQLResult result = sparqlService.executeSPARQLQuery( queryString, targetURI );

		return new ResponseEntity<>( result, HttpStatus.OK );
	}
}
