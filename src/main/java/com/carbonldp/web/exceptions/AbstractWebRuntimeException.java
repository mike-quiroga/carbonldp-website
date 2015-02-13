package com.carbonldp.web.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.carbonldp.commons.exceptions.CarbonNoStackTraceRuntimeException;

public abstract class AbstractWebRuntimeException extends CarbonNoStackTraceRuntimeException {

	private static final long serialVersionUID = - 8572467529319625869L;
	private static final HttpStatus defaultHttpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

	private HttpStatus httpStatus;

	public AbstractWebRuntimeException(int errorCode, HttpStatus httpStatus) {
		super(errorCode);
		setHttpStatus(httpStatus);
	}

	public AbstractWebRuntimeException(String message, HttpStatus httpStatus) {
		super(message);
		setHttpStatus(httpStatus);
	}

	public HttpStatus getHTTPStatus() {
		return this.httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}

	public ResponseEntity<Object> toResponseEntity() {
		HttpStatus httpStatus = getHTTPStatus();
		if ( httpStatus == null ) httpStatus = defaultHttpStatus;

		return new ResponseEntity<Object>(httpStatus);
	}
}
