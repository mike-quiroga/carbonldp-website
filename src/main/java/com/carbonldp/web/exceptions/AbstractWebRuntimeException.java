package com.carbonldp.web.exceptions;

import com.carbonldp.errors.ErrorResponse;
import com.carbonldp.errors.ErrorResponseFactory;
import com.carbonldp.exceptions.CarbonNoStackTraceRuntimeException;
import com.carbonldp.models.Infraction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public abstract class AbstractWebRuntimeException extends CarbonNoStackTraceRuntimeException {

	private static final long serialVersionUID = - 8572467529319625869L;
	private static final HttpStatus defaultHttpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	private static final Integer defaultCarbonCode = 0xF000;

	private HttpStatus httpStatus;

	public AbstractWebRuntimeException( int errorCode, HttpStatus httpStatus ) {
		super( errorCode );
		setHttpStatus( httpStatus );
	}

	public AbstractWebRuntimeException( String message, HttpStatus httpStatus ) {
		super( message );
		setHttpStatus( httpStatus );
	}
	public AbstractWebRuntimeException( Infraction infraction ) {
		super( infraction );
	}

	public HttpStatus getHTTPStatus() {
		return this.httpStatus;
	}

	public void setHttpStatus( HttpStatus httpStatus ) {
		this.httpStatus = httpStatus;
	}

	public ResponseEntity<Object> toResponseEntity() {
		HttpStatus httpStatus = getHTTPStatus();
		if ( httpStatus == null ) httpStatus = defaultHttpStatus;
		int errorCode = getErrorCode();
		if ( errorCode == 0 ) errorCode = defaultCarbonCode;
		String message = getMessage();
		if ( message == null ) message = "an unexpected error has occurred";

		ErrorResponse error = ErrorResponseFactory.create( errorCode, message, httpStatus );

		return new ResponseEntity<>( error.getBaseModel(), httpStatus );
	}
}
