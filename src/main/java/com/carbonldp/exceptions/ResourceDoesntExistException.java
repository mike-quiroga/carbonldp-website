package com.carbonldp.exceptions;

public class ResourceDoesntExistException extends CarbonNoStackTraceRuntimeException {

	private static int defaultErrorCode = 0x4001;

	public ResourceDoesntExistException( int errorCode ) {
		super( errorCode );
	}

	public ResourceDoesntExistException() {
		super( defaultErrorCode );
	}
}
