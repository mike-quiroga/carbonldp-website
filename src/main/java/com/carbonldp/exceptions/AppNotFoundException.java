package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since 0.37.0
 */
public class AppNotFoundException extends CarbonNoStackTraceRuntimeException {
	public AppNotFoundException() {
		super( new Infraction( 0x4001 ) );
	}
}
