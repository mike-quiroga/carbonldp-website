package com.carbonldp.exceptions;

import com.carbonldp.web.exceptions.BadRequestException;

/**
 * @author NestorVenegas
 * @since 0.24.0-ALPHA
 */
public class InvalidResourceURIException extends BadRequestException {
	public InvalidResourceURIException() {
		super( 0x200B );
	}
}
