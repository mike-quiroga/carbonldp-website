package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @since 0.19.0-ALPHA
 */
public class InvalidRDFTypeException extends InvalidResourceException {

	public InvalidRDFTypeException( Infraction infraction ) {
		super( infraction );
	}
}
