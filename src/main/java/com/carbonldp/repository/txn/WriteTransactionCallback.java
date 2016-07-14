package com.carbonldp.repository.txn;

import org.eclipse.rdf4j.repository.RepositoryConnection;

public interface WriteTransactionCallback {
	public void executeInTransaction( RepositoryConnection connection ) throws Exception;
}
