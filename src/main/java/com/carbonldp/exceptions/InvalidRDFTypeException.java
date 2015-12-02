package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class InvalidRDFTypeException extends InvalidResourceException {

	public InvalidRDFTypeException( Infraction infraction ) {
		super( infraction );
	}
}
