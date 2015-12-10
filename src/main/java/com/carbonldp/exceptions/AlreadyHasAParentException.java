package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @since 0.18.0-ALPHA
 */
public class AlreadyHasAParentException extends CarbonNoStackTraceRuntimeException {

	public AlreadyHasAParentException() {
		super( new Infraction( 0x200D ) );
	}
}
