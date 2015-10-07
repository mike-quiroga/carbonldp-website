package com.carbonldp.exceptions;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class NotADirectoryException extends FileException {

	public NotADirectoryException( int errorCode ) {
		super( errorCode );
	}
}
