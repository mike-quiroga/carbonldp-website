package com.carbonldp.exceptions;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class FileException extends CarbonNoStackTraceRuntimeException {

	public FileException( int errorCode ) {
		super( errorCode );
	}
}
