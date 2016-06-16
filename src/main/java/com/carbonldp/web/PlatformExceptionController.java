package com.carbonldp.web;

import com.carbonldp.exceptions.ExceptionConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class PlatformExceptionController {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );

	@ExceptionHandler( Exception.class )
	public ResponseEntity<Object> handleUnexpectedException( HttpServletRequest request, HttpServletResponse response, Exception exception ) {
		return ExceptionConverter.convertException( response, exception );
	}
}
