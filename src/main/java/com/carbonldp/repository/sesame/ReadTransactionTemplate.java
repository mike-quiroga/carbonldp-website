package com.carbonldp.repository.sesame;

public interface ReadTransactionTemplate<T> {
	public T execute(ReadTransactionCallback<T> callback);
}
