package com.carbonldp;

public class CarbonException extends RuntimeException {

	private static final long serialVersionUID = 1587001462545102227L;

	public CarbonException() {
		super();
	}

	public CarbonException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CarbonException(String message, Throwable cause) {
		super(message, cause);
	}

	public CarbonException(String message) {
		super(message);
	}

	public CarbonException(Throwable cause) {
		super(cause);
	}

}
