package com.carbonldp.web.exceptions;

import org.springframework.http.HttpStatus;

public class PreconditionRequiredException extends AbstractWebRuntimeException {
	private static final HttpStatus defaultStatus = HttpStatus.PRECONDITION_REQUIRED;

	public PreconditionRequiredException() {
		this( 0 );
	}

	public PreconditionRequiredException( int errorCode ) {
		super( errorCode, defaultStatus );
	}

	public PreconditionRequiredException( String message ) {
		super( message, defaultStatus );
	}
}
