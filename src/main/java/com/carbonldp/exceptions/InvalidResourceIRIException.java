package com.carbonldp.exceptions;

import com.carbonldp.web.exceptions.BadRequestException;

/**
 * @author NestorVenegas
 * @since 0.24.0-ALPHA
 */
public class InvalidResourceIRIException extends BadRequestException {
	public InvalidResourceIRIException() {
		super( 0x200B );
	}
}
