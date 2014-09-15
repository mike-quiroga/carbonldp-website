package com.base22.carbon.repository;

import com.base22.carbon.CarbonException;

public interface WriteTransactionTemplate {
	public void addCallback(WriteTransactionCallback callback);

	public void execute(WriteTransactionCallback callback) throws CarbonException;

	public void execute() throws CarbonException;
}
