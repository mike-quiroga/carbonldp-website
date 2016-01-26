package com.carbonldp.web;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.Vars;
import com.carbonldp.errors.ErrorResponse;
import com.carbonldp.errors.ErrorResponseFactory;
import com.carbonldp.exceptions.AuthorizationException;
import com.carbonldp.exceptions.CarbonNoStackTraceRuntimeException;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.http.Link;
import com.carbonldp.models.Infraction;
import com.carbonldp.web.exceptions.AbstractWebRuntimeException;
import com.carbonldp.web.exceptions.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
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
		if ( rawException instanceof AbstractWebRuntimeException ) return handleAbstractWebRuntimeException( (AbstractWebRuntimeException) rawException );
		int errorCode = rawException.getErrorCode();
		if ( errorCode == 0x4001 ) {
			ErrorResponse error = ErrorResponseFactory.create( 0x4001, rawException.getMessage(), HttpStatus.NOT_FOUND );
			return new ResponseEntity<>( error.getBaseModel(), HttpStatus.NOT_FOUND );
		}
		ErrorResponse error = ErrorResponseFactory.create( errorCode, rawException.getMessage(), HttpStatus.BAD_REQUEST );
		return new ResponseEntity<>( error.getBaseModel(), HttpStatus.BAD_REQUEST );

	}

	public ResponseEntity<Object> handleAbstractWebRuntimeException( AbstractWebRuntimeException exception ) {
		return exception.toResponseEntity();
	}

	@ExceptionHandler( HttpMessageNotReadableException.class )
	public ResponseEntity<Object> handleHttpMessageNotReadableException( HttpServletRequest request, HttpServletResponse response, Exception rawException ) {
		HttpMessageNotReadableException exception = (HttpMessageNotReadableException) rawException;
		return new BadRequestException( 0x6001 ).toResponseEntity();
	}

	@ExceptionHandler( Exception.class )
	public ResponseEntity<Object> handleUnexpectedException( HttpServletRequest request, HttpServletResponse response, Exception rawException ) {
		// AccessDeniedException is handled in the ExceptionTranslationFilter
		if ( rawException instanceof AccessDeniedException ) throw (AccessDeniedException) rawException;
		if ( LOG.isErrorEnabled() ) LOG.debug( "<< handleUnexpectedException() > Exception Stacktrace: ", rawException );
		ErrorResponse error = ErrorResponseFactory.create( new Infraction( 0xFFFF ), HttpStatus.INTERNAL_SERVER_ERROR );

		return new ResponseEntity<>( error.getBaseModel(), HttpStatus.INTERNAL_SERVER_ERROR );
	}

	@ExceptionHandler( AuthorizationException.class )
	public ResponseEntity<Object> handleAuthorizationException( HttpServletRequest request, HttpServletResponse response, AuthorizationException exception ) {
		ErrorResponse error = ErrorResponseFactory.create( exception.getErrorCode(), exception.getMessage(), HttpStatus.FORBIDDEN );
		return new ResponseEntity<>( error.getBaseModel(), HttpStatus.FORBIDDEN );
	}

	@ExceptionHandler( InvalidResourceException.class )
	public ResponseEntity<Object> handleIllegalArgumentException( HttpServletRequest request, HttpServletResponse response, InvalidResourceException exception ) {
		ErrorResponse error = ErrorResponseFactory.create( exception.getInfractions(), HttpStatus.BAD_REQUEST );
		addConstrainedByLinkHeader( response, Vars.getInstance().getAPIResourceURL() );
		return new ResponseEntity<>( error.getBaseModel(), HttpStatus.BAD_REQUEST );
	}

	@ExceptionHandler( HttpMediaTypeNotSupportedException.class )
	public ResponseEntity<Object> handleHttpMediaTypeNotSupportedException( HttpServletRequest request, HttpServletResponse response, HttpMediaTypeNotSupportedException exception ) {
		return new BadRequestException( 0x6002 ).toResponseEntity();
	}

	private void addConstrainedByLinkHeader( HttpServletResponse response, String restringedBy ) {
		Link link = new Link( restringedBy );
		link.addRelationshipType( "http://www.w3.org/ns/ldp#constrainedBy" );
		response.addHeader( HTTPHeaders.LINK, link.toString() );

	}

}
