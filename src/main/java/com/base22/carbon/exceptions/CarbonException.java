package com.base22.carbon.exceptions;

import com.base22.carbon.models.ErrorResponse;
import com.base22.carbon.models.ErrorResponseFactory;

public class CarbonException extends Exception {

	private static final long serialVersionUID = - 2943983499420713616L;

	protected ErrorResponse errorObject;

	public CarbonException(ErrorResponse errorObject) {
		this.errorObject = errorObject;
	}

	public CarbonException(String friendlyMessage) {
		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		errorObject = errorFactory.create();
		errorObject.setFriendlyMessage(friendlyMessage);
	}

	public CarbonException(String friendlyMessage, String debugMessage) {
		ErrorResponseFactory errorFactory = new ErrorResponseFactory();
		errorObject = errorFactory.create();
		errorObject.setFriendlyMessage(friendlyMessage);
		errorObject.setDebugMessage(debugMessage);
	}

	public ErrorResponse getErrorObject() {
		return errorObject;
	}
}
