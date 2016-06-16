package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since _version_
 */
public class AppNotFoundException extends CarbonNoStackTraceRuntimeException {
	public AppNotFoundException() {
		super( new Infraction( 0x4001 ) );
	}
}
