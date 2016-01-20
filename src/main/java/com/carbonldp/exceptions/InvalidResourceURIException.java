package com.carbonldp.exceptions;

import com.carbonldp.web.exceptions.BadRequestException;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class InvalidResourceURIException extends BadRequestException {
	public InvalidResourceURIException() {
		super( 0x200B );
	}
}
