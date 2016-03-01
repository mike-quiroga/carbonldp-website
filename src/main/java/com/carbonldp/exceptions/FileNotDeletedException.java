package com.carbonldp.exceptions;

/**
 * @author NestorEstrada
 * @since 0.28.0-ALPHA
 */
public class FileNotDeletedException extends FileException {
	private static final int dfaultErrorCode = 0x1010;

	public FileNotDeletedException( int errorCode ) {
		super( errorCode );
	}
}
