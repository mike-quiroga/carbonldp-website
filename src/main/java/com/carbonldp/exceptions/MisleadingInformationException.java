package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class MisleadingInformationException extends CarbonNoStackTraceRuntimeException {

	public MisleadingInformationException( int errorCode ) {
		super( errorCode );
	}

	public MisleadingInformationException( Infraction infraction ) {
		super( infraction );
	}

}
