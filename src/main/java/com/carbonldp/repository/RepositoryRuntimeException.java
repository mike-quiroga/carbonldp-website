package com.carbonldp.repository;

import com.carbonldp.CarbonException;

public class RepositoryRuntimeException extends CarbonException {
	private static final long serialVersionUID = - 8852740381268423530L;

	public RepositoryRuntimeException() {
		super();
	}

	public RepositoryRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public RepositoryRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public RepositoryRuntimeException(String message) {
		super(message);
	}

	public RepositoryRuntimeException(Throwable cause) {
		super(cause);
	}

}
