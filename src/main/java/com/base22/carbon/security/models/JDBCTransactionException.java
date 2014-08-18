package com.base22.carbon.security.models;

import com.base22.carbon.exceptions.CarbonException;

public class JDBCTransactionException extends CarbonException {

	private static final long serialVersionUID = 8905093704891217685L;

	public JDBCTransactionException(String message) {
		super(message);
	}

}
