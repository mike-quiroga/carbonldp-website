package com.carbonldp.repository.txn;

import org.eclipse.rdf4j.repository.RepositoryConnection;

public interface ReadTransactionCallback<T> {
	public T executeInTransaction( RepositoryConnection connection ) throws Exception;
}
