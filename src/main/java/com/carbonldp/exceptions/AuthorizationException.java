package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class AuthorizationException extends CarbonNoStackTraceRuntimeException {

	public AuthorizationException( Infraction infraction ) {
		super( infraction );
	}
}
