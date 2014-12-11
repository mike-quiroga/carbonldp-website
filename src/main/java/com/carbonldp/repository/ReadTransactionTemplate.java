package com.carbonldp.repository;

public interface ReadTransactionTemplate<T> {
	public T execute(ReadTransactionCallback<T> callback);
}
