package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since _version_
 */
public class LDAPException extends CarbonNoStackTraceRuntimeException {

	public LDAPException( Infraction infraction ) {
		super( infraction );
	}
}
