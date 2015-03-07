package com.carbonldp.web;

import com.carbonldp.web.exceptions.AbstractWebRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class PlatformExceptionController {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );

	@ExceptionHandler( AbstractWebRuntimeException.class )
	public ResponseEntity<Object> handleAbstractWebRuntimeException(HttpServletRequest request, HttpServletResponse response, Exception rawException) {
		AbstractWebRuntimeException exception = (AbstractWebRuntimeException) rawException;
		return exception.toResponseEntity();
	}

	@ExceptionHandler( Exception.class )
	public ResponseEntity<Object> handleUnexpectedException(HttpServletRequest request, HttpServletResponse response, Exception rawException) {
		if ( LOG.isDebugEnabled() ) {
			LOG.debug( "<< handleUnexpectedException() > Exception Stacktrace: ", rawException );
		}

		// TODO: Create RDF error description

		return new ResponseEntity<Object>( rawException.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR );
	}
}
