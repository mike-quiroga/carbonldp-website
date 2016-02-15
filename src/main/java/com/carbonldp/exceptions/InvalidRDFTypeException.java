package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @since 0.19.0-ALPHA
 */
public class InvalidRDFTypeException extends InvalidResourceException {
	private static final int DEFAULT_ERROR_CODE = 0x2001;

	public InvalidRDFTypeException( Infraction infraction ) {
		super( infraction );
	}

	public InvalidRDFTypeException( String rdfType ) {
		super( new Infraction( DEFAULT_ERROR_CODE, "rdf.type", rdfType ) );
	}
}
