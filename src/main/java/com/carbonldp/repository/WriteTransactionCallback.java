package com.carbonldp.repository;

import org.openrdf.repository.RepositoryConnection;

public interface WriteTransactionCallback {
	public void executeInTransaction(RepositoryConnection connection) throws Exception;
}
