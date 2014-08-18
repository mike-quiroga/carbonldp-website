package com.base22.carbon.exceptions;

public class RdfServiceException extends CarbonException{
	
	private static final long serialVersionUID = 3320573094649912768L;
	private String message;
	private Exception originalException;

	public RdfServiceException(String message) {
		super(message);
		this.message = message;
	}
	public RdfServiceException(String message, Exception originalException) {
		super(message);
		this.message = message;
		this.originalException = originalException;
	}
	public String getMessage() {
		return message;
	}
	public Exception getOriginalException(){
		return originalException;
	}
}
