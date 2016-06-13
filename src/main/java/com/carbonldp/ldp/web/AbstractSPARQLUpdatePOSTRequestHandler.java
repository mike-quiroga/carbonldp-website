package com.carbonldp.ldp.web;

import com.carbonldp.web.RequestHandler;
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
		// TODO
		sparqlService.executeSPARQLUpdate( "", null );
		return null;
	}
}
