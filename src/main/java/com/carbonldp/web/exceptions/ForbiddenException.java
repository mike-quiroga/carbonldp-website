package com.carbonldp.web.exceptions;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends AbstractWebRuntimeException {
	private static final HttpStatus defaultStatus = HttpStatus.FORBIDDEN;

	public ForbiddenException( int errorCode ) {
		super( errorCode, defaultStatus );
	}

	public ForbiddenException( String message ) {
		super( message, defaultStatus );
	}
}
