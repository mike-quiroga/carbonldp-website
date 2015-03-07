package com.carbonldp.repository.txn;

import com.carbonldp.exceptions.CarbonRuntimeException;

public class RepositoryRuntimeException extends CarbonRuntimeException {
	private static final long serialVersionUID = -8852740381268423530L;

	public RepositoryRuntimeException(int errorCode) {
		super( errorCode );
	}

	public RepositoryRuntimeException(int errorCode, Throwable cause) {
		super( errorCode, cause );
	}

	public RepositoryRuntimeException(Throwable cause) {
		super( cause );
	}

}
