package com.carbonldp.web.exceptions;

import org.springframework.http.HttpStatus;

public class PreconditionRequiredException extends AbstractWebRuntimeException {
	private static final HttpStatus defaultStatus = HttpStatus.PRECONDITION_REQUIRED;
	private static final int defaultErrorCode = 0x5007;

	public PreconditionRequiredException() {
		this( defaultErrorCode );
	}

	public PreconditionRequiredException( int errorCode ) {
		super( errorCode, defaultStatus );
	}

	public PreconditionRequiredException( String message ) {
		super( message, defaultStatus );
	}
}
