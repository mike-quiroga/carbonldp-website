package com.carbonldp.repository;

import org.openrdf.repository.RepositoryConnection;

public interface ReadTransactionCallback<T> {
	public T executeInTransaction(RepositoryConnection connection) throws Exception;
}
