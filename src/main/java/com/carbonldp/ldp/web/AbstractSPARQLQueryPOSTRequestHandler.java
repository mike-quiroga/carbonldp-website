package com.carbonldp.ldp.web;

import com.carbonldp.sparql.SPARQLResult;
import com.carbonldp.web.RequestHandler;
import com.carbonldp.web.exceptions.NotFoundException;
import org.openrdf.model.IRI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequestHandler
public class AbstractSPARQLQueryPOSTRequestHandler extends AbstractLDPRequestHandler {
	public ResponseEntity<Object> handleRequest( String queryString, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );
		//TODO: check if this is well handled by w3c specs
		if ( request.getHeader( "default-graph-uri" ) != null || request.getHeader( "default-graph-uri" ) != null )
			return new ResponseEntity<>( HttpStatus.BAD_REQUEST );
		IRI targetIRI = getTargetIRI( request );

		SPARQLResult result = sparqlService.executeSPARQLQuery( queryString, targetIRI );

		return new ResponseEntity<>( result, HttpStatus.OK );
	}
}
