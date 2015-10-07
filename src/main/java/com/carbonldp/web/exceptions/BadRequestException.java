package com.carbonldp.web.exceptions;

import com.carbonldp.models.Infraction;
import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractWebRuntimeException {
	private static final long serialVersionUID = - 9029732779805736874L;
	private static final HttpStatus defaultStatus = HttpStatus.BAD_REQUEST;

	public BadRequestException( int errorCode ) {
		super( errorCode, defaultStatus );
	}

	public BadRequestException( String message ) {
		super( message, defaultStatus );
	}

	public BadRequestException( Infraction infraction ) {
		super( infraction );
	}
}
