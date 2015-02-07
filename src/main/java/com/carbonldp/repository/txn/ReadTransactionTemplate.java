package com.carbonldp.repository.txn;

public interface ReadTransactionTemplate<T> {
	public T execute(ReadTransactionCallback<T> callback);
}
