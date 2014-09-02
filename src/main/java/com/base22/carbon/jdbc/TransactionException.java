package com.base22.carbon.jdbc;

import com.base22.carbon.CarbonException;

public class TransactionException extends CarbonException {

	private static final long serialVersionUID = 8905093704891217685L;

	public TransactionException(String message) {
		super(message);
	}

}
