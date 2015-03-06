package com.carbonldp.web.exceptions;

import org.springframework.http.HttpStatus;

public class NotImplementedException extends AbstractWebRuntimeException {
	private static final long serialVersionUID = 7642670009783174804L;
	private static final HttpStatus defaultStatus = HttpStatus.NOT_IMPLEMENTED;

	public NotImplementedException() {
		this( 0 );
	}

	public NotImplementedException(int errorCode) {
		super( errorCode, defaultStatus );
	}

	public NotImplementedException(String message) {
		super( message, defaultStatus );
	}

}
