package com.carbonldp.exceptions;

import com.carbonldp.models.Infraction;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class AlreadyHasAParentException extends CarbonNoStackTraceRuntimeException {

	public AlreadyHasAParentException() {
		super( new Infraction( 0x200D ) );
	}
}
