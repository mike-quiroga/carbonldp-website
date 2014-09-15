package com.base22.carbon.repository;

import com.base22.carbon.CarbonException;

public interface ReadTransactionTemplate<T> {
	public T execute(ReadTransactionCallback<T> callback) throws CarbonException;
}
