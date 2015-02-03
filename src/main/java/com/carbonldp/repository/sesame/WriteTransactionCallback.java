package com.carbonldp.repository.sesame;

import org.openrdf.repository.RepositoryConnection;

public interface WriteTransactionCallback {
	public void executeInTransaction(RepositoryConnection connection) throws Exception;
}
