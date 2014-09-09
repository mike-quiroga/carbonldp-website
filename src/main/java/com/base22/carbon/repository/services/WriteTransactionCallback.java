package com.base22.carbon.repository.services;

import com.hp.hpl.jena.query.Dataset;

public interface WriteTransactionCallback {
	public void executeInTransaction(Dataset dataset) throws Exception;
}
