package com.carbonldp.web.exceptions;

import com.carbonldp.models.Infraction;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class ForbiddenException extends AbstractWebRuntimeException {
	private static final HttpStatus defaultStatus = HttpStatus.FORBIDDEN;
	private static final int defaultErrorCode = 0x7001;

	private List<Infraction> infractions;

	public ForbiddenException( Infraction infraction ) {
		super( defaultErrorCode, defaultStatus );
		infractions = new ArrayList<>();
		infractions.add( infraction );
	}

	public ForbiddenException( int errorCode ) {
		super( errorCode, defaultStatus );
	}

	public ForbiddenException( String message ) {
		super( message, defaultStatus );
	}
}
