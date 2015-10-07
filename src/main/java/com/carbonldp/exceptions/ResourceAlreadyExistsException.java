package com.carbonldp.exceptions;

public class ResourceAlreadyExistsException extends CarbonNoStackTraceRuntimeException {
	private static int defaultErrorCode = 0x2008;

	public ResourceAlreadyExistsException( int errorCode ) {
		super( errorCode );
	}

	public ResourceAlreadyExistsException() {
		super( defaultErrorCode );
	}
}
