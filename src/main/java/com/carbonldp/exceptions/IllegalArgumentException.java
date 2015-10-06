package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class IllegalArgumentException extends CarbonNoStackTraceRuntimeException {

	public IllegalArgumentException( int errorCode ) {
		super( errorCode );
	}

	public IllegalArgumentException( Infraction infraction ) {
		super( infraction );
	}

}
