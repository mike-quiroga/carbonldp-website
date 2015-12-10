package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @since 0.18.0-ALPHA
 */
public class AuthorizationException extends CarbonNoStackTraceRuntimeException {

	public AuthorizationException( Infraction infraction ) {
		super( infraction );
	}
}
