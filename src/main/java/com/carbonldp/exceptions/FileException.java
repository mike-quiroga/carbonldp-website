package com.carbonldp.exceptions;

/**
 * @author NestorVenegas
 * @since 0.28.0-ALPHA
 */
public class FileException extends CarbonNoStackTraceRuntimeException {

	public FileException( int errorCode ) {
		super( errorCode );
	}
}
