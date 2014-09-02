package com.base22.carbon.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;

@ControllerAdvice
public class PlatformExceptionController {

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Object> handleAccessDenied(HttpServletRequest request, HttpServletResponse response, Exception rawException) {
		AccessDeniedException exception = (AccessDeniedException) rawException;

		// TODO: Find a way of knowing why was the accessDenied thrown
		String friendlyMessage = "You don't have the required permission to complete the request.";
		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handleAccessDenied() > The access was denied.");
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setFriendlyMessage(friendlyMessage);

		return new ResponseEntity<Object>(errorObject, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleUnexpectedException(HttpServletRequest request, HttpServletResponse response, Exception rawException) {
		String friendlyMessage = "An unexpected exception occurred.";
		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< handleAccessDenied() > Exception Stacktrace:", rawException);
		}

		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		ErrorResponse errorObject = errorFactory.create();
		errorObject.setFriendlyMessage(friendlyMessage);

		return new ResponseEntity<Object>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
