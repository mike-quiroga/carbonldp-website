package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since 0.37.0
 */
public class LDAPException extends CarbonNoStackTraceRuntimeException {

	public LDAPException( Infraction infraction ) {
		super( infraction );
	}
}
