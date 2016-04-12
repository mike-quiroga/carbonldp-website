package com.carbonldp.ldp.web;

import com.carbonldp.Consts;
import com.carbonldp.HTTPHeaders;
import com.carbonldp.http.Link;
import org.openrdf.model.IRI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AbstractOPTIONSRequestHandler extends AbstractLDPRequestHandler {
	public ResponseEntity<Object> handleRequest( HttpServletRequest request, HttpServletResponse response ) {

		response.addHeader( HTTPHeaders.ACCEPT_POST, "application/ld+json, text/turtle" );
		response.addHeader( HTTPHeaders.ACCEPT_PUT, "application/ld+json, text/turtle" );
		response.addHeader( HTTPHeaders.ACCEPT_PATCH, "application/ld+json, text/turtle" );
		IRI resourceIRI = getTargetIRI( request );
		if ( isRDFRepresentation( resourceIRI ) ) addDescribedByHeader( response, resourceIRI );

		return new ResponseEntity<>( HttpStatus.OK );
	}

	private boolean isRDFRepresentation( IRI resourceIRI ) {
		return nonRdfSourceService.isRDFRepresentation( resourceIRI );
	}

	private void addDescribedByHeader( HttpServletResponse response, IRI resourceIRI ) {

		Link link = new Link( resourceIRI.stringValue() );
		link.addRelationshipType( Consts.DESCRIBED_BY );
		link.setAnchor( resourceIRI.stringValue() );
		response.addHeader( HTTPHeaders.LINK, link.toString() );
	}
}
