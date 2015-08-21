package com.carbonldp.exceptions;

public class StupidityException extends CarbonRuntimeException {
	private static final long serialVersionUID = 4349408696866966205L;

	public StupidityException( Throwable cause ) {
		super( cause );
	}

	public StupidityException( String message ) {
		super( message );
	}
}
