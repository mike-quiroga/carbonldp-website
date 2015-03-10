package com.carbonldp.web.exceptions;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AbstractWebRuntimeException {
	private static final HttpStatus defaultStatus = HttpStatus.UNAUTHORIZED;

	public UnauthorizedException( int errorCode ) {
		super( errorCode, defaultStatus );
	}

	public UnauthorizedException( String message ) {
		super( message, defaultStatus );
	}
}
