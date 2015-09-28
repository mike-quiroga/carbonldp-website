package com.carbonldp.web;

import com.carbonldp.errors.ErrorResponse;
import com.carbonldp.errors.ErrorResponseFactory;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.web.exceptions.AbstractWebRuntimeException;
import com.carbonldp.web.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class PlatformExceptionController {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );

	@ExceptionHandler( AbstractWebRuntimeException.class )
	public ResponseEntity<Object> handleAbstractWebRuntimeException( HttpServletRequest request, HttpServletResponse response, Exception rawException ) {
		AbstractWebRuntimeException exception = (AbstractWebRuntimeException) rawException;
		return exception.toResponseEntity();
	}

	@ExceptionHandler( HttpMessageNotReadableException.class )
	public ResponseEntity<Object> handleHttpMessageNotReadableException( HttpServletRequest request, HttpServletResponse response, Exception rawException ) {
		HttpMessageNotReadableException exception = (HttpMessageNotReadableException) rawException;
		return new BadRequestException( exception.getMessage() ).toResponseEntity();
	}

	@ExceptionHandler( Exception.class )
	public ResponseEntity<Object> handleUnexpectedException( HttpServletRequest request, HttpServletResponse response, Exception rawException ) {
		// AccessDeniedException is handled in the ExceptionTranslationFilter
		if ( rawException instanceof AccessDeniedException ) throw (AccessDeniedException) rawException;
		if ( LOG.isDebugEnabled() ) LOG.debug( "<< handleUnexpectedException() > Exception Stacktrace: ", rawException );
		ErrorResponse error = ErrorResponseFactory.create( 0xF000,"there was an unexpected error", HttpStatus.INTERNAL_SERVER_ERROR );

		return new ResponseEntity<>( error, HttpStatus.INTERNAL_SERVER_ERROR );
	}

	@ExceptionHandler( InvalidResourceException.class )
	public ResponseEntity<Object> handleIllegalArgumentException( HttpServletRequest request, HttpServletResponse response, InvalidResourceException exception ) {
		ErrorResponse error = ErrorResponseFactory.create( exception.getInfractions(), HttpStatus.BAD_REQUEST );

		return new ResponseEntity<>( error, HttpStatus.BAD_REQUEST );
	}
}
