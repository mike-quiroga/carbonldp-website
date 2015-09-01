package com.carbonldp.ldp.web;

import com.carbonldp.Consts;
import com.carbonldp.HTTPHeaders;
import com.carbonldp.http.Link;
import org.openrdf.model.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractOPTIONSRequestHandler extends AbstractLDPRequestHandler {
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {

		response.addHeader( HTTPHeaders.ACCEPT_POST, "application/ld+json, text/turtle" );
		response.addHeader( HTTPHeaders.ACCEPT_PUT, "application/ld+json, text/turtle" );
		response.addHeader( HTTPHeaders.ACCEPT_PATCH, "application/ld+json, text/turtle" );
		URI resourceURI = getTargetURI( request );
		if ( isRDFRepresentation( resourceURI ) ) addDescribedByHeader( response, resourceURI );

		return new ResponseEntity<>( HttpStatus.OK );
	}

	private boolean isRDFRepresentation( URI resourceURI ) {
		return nonRdfSourceService.isRDFRepresentation( resourceURI );
	}

	private void addDescribedByHeader( HttpServletResponse response, URI resourceURI ) {

		Link link = new Link( resourceURI.stringValue() );
		link.addRelationshipType( Consts.DESCRIBED_BY );
		link.setAnchor( resourceURI.stringValue() );
		response.addHeader( HTTPHeaders.LINK, link.toString() );
	}
}
