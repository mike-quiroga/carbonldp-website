package com.base22.carbon.repository.services;

import com.base22.carbon.CarbonException;

public interface WriteTransactionTemplate {
	public void execute(WriteTransactionCallback callback) throws CarbonException;
}
