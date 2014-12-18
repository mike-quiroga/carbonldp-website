package com.carbonldp;

public class CarbonRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1587001462545102227L;

	protected int errorCode;

	public CarbonRuntimeException(int errorCode) {
		super();

		this.errorCode = errorCode;
	}

	public CarbonRuntimeException(int errorCode, Throwable cause) {
		super(cause);

		this.errorCode = errorCode;
	}

	public CarbonRuntimeException(Throwable cause) {
		super(cause);
	}

}
