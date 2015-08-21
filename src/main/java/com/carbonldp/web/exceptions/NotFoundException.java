package com.carbonldp.web.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AbstractWebRuntimeException {
	private static final long serialVersionUID = 2729058903804280565L;
	private static final HttpStatus defaultStatus = HttpStatus.NOT_FOUND;

	public NotFoundException( int errorCode ) {
		super( errorCode, defaultStatus );
	}

	public NotFoundException( String message ) {
		super( message, defaultStatus );
	}
}
