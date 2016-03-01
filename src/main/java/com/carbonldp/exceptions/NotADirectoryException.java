package com.carbonldp.exceptions;

/**
 * @author NestorVenegas
 * @since 0.28.0-ALPHA
 */
public class NotADirectoryException extends FileException {

	public NotADirectoryException( int errorCode ) {
		super( errorCode );
	}
}
