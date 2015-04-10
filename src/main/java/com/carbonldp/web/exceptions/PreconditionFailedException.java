package com.carbonldp.web.exceptions;

import org.springframework.http.HttpStatus;

public class PreconditionFailedException extends AbstractWebRuntimeException {
	private static final HttpStatus defaultStatus = HttpStatus.PRECONDITION_FAILED;

	public PreconditionFailedException() {
		this( 0 );
	}

	public PreconditionFailedException( int errorCode ) {
		super( errorCode, defaultStatus );
	}

	public PreconditionFailedException( String message ) {
		super( message, defaultStatus );
	}
}
