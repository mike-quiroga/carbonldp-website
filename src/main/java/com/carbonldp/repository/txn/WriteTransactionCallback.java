package com.carbonldp.repository.txn;

import org.openrdf.repository.RepositoryConnection;

public interface WriteTransactionCallback {
	public void executeInTransaction( RepositoryConnection connection ) throws Exception;
}
