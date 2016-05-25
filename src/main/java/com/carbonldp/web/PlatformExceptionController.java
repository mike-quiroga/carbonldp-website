package com.carbonldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.Vars;
import com.carbonldp.errors.ErrorResponse;
import com.carbonldp.errors.ErrorResponseFactory;
import com.carbonldp.exceptions.AuthorizationException;
import com.carbonldp.exceptions.CarbonNoStackTraceRuntimeException;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.http.Link;
import com.carbonldp.exceptions.ExceptionUtil;
import com.carbonldp.web.exceptions.AbstractWebRuntimeException;
import com.carbonldp.web.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class PlatformExceptionController {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );

	@ExceptionHandler( CarbonNoStackTraceRuntimeException.class )
	public ResponseEntity<Object> handleNoStackTRaceRuntimeException( HttpServletRequest request, HttpServletResponse response, CarbonNoStackTraceRuntimeException rawException ) {
		return ExceptionUtil.handleNoStackTRaceRuntimeException( rawException );

	}

	@ExceptionHandler( HttpMessageNotReadableException.class )
	public ResponseEntity<Object> handleHttpMessageNotReadableException( HttpServletRequest request, HttpServletResponse response, Exception rawException ) {
		return ExceptionUtil.handleHttpMessageNotReadableException();
	}

	@ExceptionHandler( Exception.class )
	public ResponseEntity<Object> handleUnexpectedException( HttpServletRequest request, HttpServletResponse response, Exception rawException ) {
		return ExceptionUtil.handleUnexpectedException( rawException );
	}

	@ExceptionHandler( AuthorizationException.class )
	public ResponseEntity<Object> handleAuthorizationException( HttpServletRequest request, HttpServletResponse response, AuthorizationException exception ) {
		return ExceptionUtil.handleAuthorizationException( exception );
	}

	@ExceptionHandler( InvalidResourceException.class )
	public ResponseEntity<Object> handleIllegalArgumentException( HttpServletRequest request, HttpServletResponse response, InvalidResourceException exception ) {
		return ExceptionUtil.handleIllegalArgumentException( response, exception );
	}

	@ExceptionHandler( HttpMediaTypeNotSupportedException.class )
	public ResponseEntity<Object> handleHttpMediaTypeNotSupportedException( HttpServletRequest request, HttpServletResponse response, HttpMediaTypeNotSupportedException exception ) {
		return ExceptionUtil.handleHttpMediaTypeNotSupportedException();
	}

}
