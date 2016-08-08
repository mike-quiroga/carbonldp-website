package com.carbonldp.ldp.web;

import com.carbonldp.web.RequestHandler;
import org.eclipse.rdf4j.model.IRI;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author MiguelAraCo
 * @since 0.39.0
 */
@RequestHandler
public class AbstractSPARQLUpdatePOSTRequestHandler extends AbstractLDPRequestHandler {
	public ResponseEntity<Object> handleRequest( String queryString, HttpServletRequest request, HttpServletResponse response ) {
		setUp( request, response );
		IRI targetIRI = getTargetIRI( request );
		sparqlService.executeSPARQLUpdate( queryString, targetIRI );
		return null;
	}
}
