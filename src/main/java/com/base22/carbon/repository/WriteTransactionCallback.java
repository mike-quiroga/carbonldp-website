package com.base22.carbon.repository;

import com.hp.hpl.jena.query.Dataset;

public interface WriteTransactionCallback {
	public void executeInTransaction(Dataset dataset, TransactionNamedModelCache namedModelCache) throws Exception;
}
