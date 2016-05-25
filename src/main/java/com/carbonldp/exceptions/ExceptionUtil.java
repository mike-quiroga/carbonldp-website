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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since _version_
 */
public final class ExceptionUtil {

	private ExceptionUtil() {}

	public static ResponseEntity<Object> handleUnexpectedException( Exception rawException ) {//
		// AccessDeniedException is handled in the ExceptionTranslationFilter
		if ( rawException instanceof AccessDeniedException ) throw (AccessDeniedException) rawException;
		ErrorResponse error = ErrorResponseFactory.create( new Infraction( 0xFFFF ), HttpStatus.INTERNAL_SERVER_ERROR );

		return new ResponseEntity<>( error.getBaseModel(), HttpStatus.INTERNAL_SERVER_ERROR );
	}

	public static ResponseEntity<Object> handleNoStackTRaceRuntimeException( CarbonNoStackTraceRuntimeException rawException ) {//
		if ( rawException instanceof AbstractWebRuntimeException ) return handleAbstractWebRuntimeException( (AbstractWebRuntimeException) rawException );
		int errorCode = rawException.getErrorCode();
		if ( errorCode == 0x4001 ) {
			ErrorResponse error = ErrorResponseFactory.create( 0x4001, rawException.getMessage(), HttpStatus.NOT_FOUND );
			return new ResponseEntity<>( error.getBaseModel(), HttpStatus.NOT_FOUND );
		}
		ErrorResponse error = ErrorResponseFactory.create( errorCode, rawException.getMessage(), HttpStatus.BAD_REQUEST );
		return new ResponseEntity<>( error.getBaseModel(), HttpStatus.BAD_REQUEST );

	}

	public static ResponseEntity<Object> handleHttpMessageNotReadableException() {//
		return new BadRequestException( 0x6001 ).toResponseEntity();
	}

	public static ResponseEntity<Object> handleAuthorizationException( AuthorizationException exception ) {//
		ErrorResponse error = ErrorResponseFactory.create( exception.getErrorCode(), exception.getMessage(), HttpStatus.FORBIDDEN );
		return new ResponseEntity<>( error.getBaseModel(), HttpStatus.FORBIDDEN );
	}

	public static ResponseEntity<Object> handleIllegalArgumentException( HttpServletResponse response, InvalidResourceException exception ) {
		ErrorResponse error = ErrorResponseFactory.create( exception.getInfractions(), HttpStatus.BAD_REQUEST );
		addConstrainedByLinkHeader( response, Vars.getInstance().getAPIResourceURL() );
		return new ResponseEntity<>( error.getBaseModel(), HttpStatus.BAD_REQUEST );
	}

	public static ResponseEntity<Object> handleHttpMediaTypeNotSupportedException() {
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
