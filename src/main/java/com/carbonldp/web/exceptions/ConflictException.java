package com.carbonldp.web.exceptions;

import org.springframework.http.HttpStatus;

public class ConflictException extends AbstractWebRuntimeException {
	private static final long serialVersionUID = - 4115026248185675639L;
	private static final HttpStatus defaultStatus = HttpStatus.CONFLICT;

	public ConflictException( int errorCode ) {
		super( errorCode, defaultStatus );
	}

	public ConflictException( String message ) {
		super( message, defaultStatus );
	}
}
