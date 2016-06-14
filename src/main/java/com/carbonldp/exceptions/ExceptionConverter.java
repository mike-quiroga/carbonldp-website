package com.carbonldp.exceptions;

import com.carbonldp.HTTPHeaders;
import com.carbonldp.Vars;
import com.carbonldp.errors.ErrorResponse;
import com.carbonldp.errors.ErrorResponseFactory;
import com.carbonldp.exceptions.CarbonNoStackTraceRuntimeException;
import com.carbonldp.http.Link;
import com.carbonldp.models.Infraction;
import com.carbonldp.web.exceptions.AbstractWebRuntimeException;
import com.carbonldp.web.exceptions.BadRequestException;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since _version_
 */
public final class ExceptionConverter {
	protected static final Logger LOG = LoggerFactory.getLogger( ExceptionConverter.class );

	private ExceptionConverter() {}

	public static ResponseEntity<Object> handleException( HttpServletResponse response, Exception exception ) {
		if ( exception instanceof AuthorizationException ) {
			return handleAuthorizationException( (AuthorizationException) exception );
		} else if ( exception instanceof HttpMessageNotReadableException ) {
			return handleHttpMessageNotReadableException();
		} else if ( exception instanceof CarbonNoStackTraceRuntimeException ) {
			return handleNoStackTraceRuntimeException( (CarbonNoStackTraceRuntimeException) exception );
		} else if ( exception instanceof InvalidResourceException ) {
			return handleIllegalArgumentException( response, (InvalidResourceException) exception );
		} else if ( exception instanceof HttpMediaTypeNotSupportedException ) {
			return ExceptionConverter.handleHttpMediaTypeNotSupportedException();
		} else if ( exception instanceof Exception ) {
			LOG.error( "An exception reached the top of the chain. Exception: {}", exception );
			return ExceptionConverter.handleUnexpectedException( exception );
		} else {
			LOG.error( "An error reached the top of the chain. Exception: {}", exception );
			return handleUnexpectedException();
		}
	}

	protected static ResponseEntity<Object> handleUnexpectedException() {//
		ErrorResponse error = ErrorResponseFactory.create( new Infraction( 0xFFFF ), HttpStatus.INTERNAL_SERVER_ERROR, ThreadContext.get( "requestID" ) );
		return new ResponseEntity<>( error.getBaseModel(), HttpStatus.INTERNAL_SERVER_ERROR );
	}

	protected static ResponseEntity<Object> handleUnexpectedException( Exception rawException ) {//
		// AccessDeniedException is handled in the ExceptionTranslationFilter
		if ( rawException instanceof AccessDeniedException ) throw (AccessDeniedException) rawException;
		ErrorResponse error = ErrorResponseFactory.create( new Infraction( 0xFFFF ), HttpStatus.INTERNAL_SERVER_ERROR, ThreadContext.get( "requestID" ) );

		return new ResponseEntity<>( error.getBaseModel(), HttpStatus.INTERNAL_SERVER_ERROR );
	}

	protected static ResponseEntity<Object> handleNoStackTraceRuntimeException( CarbonNoStackTraceRuntimeException rawException ) {//
		if ( rawException instanceof AbstractWebRuntimeException ) return handleAbstractWebRuntimeException( (AbstractWebRuntimeException) rawException );
		int errorCode = rawException.getErrorCode();
		if ( errorCode == 0x4001 ) {
			ErrorResponse error = ErrorResponseFactory.create( 0x4001, rawException.getMessage(), HttpStatus.NOT_FOUND, ThreadContext.get( "requestID" ) );
			return new ResponseEntity<>( error.getBaseModel(), HttpStatus.NOT_FOUND );
		}
		ErrorResponse error = ErrorResponseFactory.create( errorCode, rawException.getMessage(), HttpStatus.BAD_REQUEST, ThreadContext.get( "requestID" ) );
		return new ResponseEntity<>( error.getBaseModel(), HttpStatus.BAD_REQUEST );

	}

	protected static ResponseEntity<Object> handleHttpMessageNotReadableException() {//
		return new BadRequestException( 0x6001 ).toResponseEntity();
	}

	protected static ResponseEntity<Object> handleAuthorizationException( AuthorizationException exception ) {//
		ErrorResponse error = ErrorResponseFactory.create( exception.getErrorCode(), exception.getMessage(), HttpStatus.FORBIDDEN, ThreadContext.get( "requestID" ) );
		return new ResponseEntity<>( error.getBaseModel(), HttpStatus.FORBIDDEN );
	}

	protected static ResponseEntity<Object> handleIllegalArgumentException( HttpServletResponse response, InvalidResourceException exception ) {
		ErrorResponse error = ErrorResponseFactory.create( exception.getInfractions(), HttpStatus.BAD_REQUEST, ThreadContext.get( "requestID" ) );
		addConstrainedByLinkHeader( response, Vars.getInstance().getAPIResourceURL() );
		return new ResponseEntity<>( error.getBaseModel(), HttpStatus.BAD_REQUEST );
	}

	protected static ResponseEntity<Object> handleHttpMediaTypeNotSupportedException() {
		return new BadRequestException( 0x6002 ).toResponseEntity();
	}

	private static void addConstrainedByLinkHeader( HttpServletResponse response, String restringedBy ) {
		Link link = new Link( restringedBy );
		link.addRelationshipType( "http://www.w3.org/ns/ldp#constrainedBy" );
		response.addHeader( HTTPHeaders.LINK, link.toString() );

	}

	private static ResponseEntity<Object> handleAbstractWebRuntimeException( AbstractWebRuntimeException exception ) {
		return exception.toResponseEntity();
	}
}
