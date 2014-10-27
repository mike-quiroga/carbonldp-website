package com.base22.carbon.repository;

import com.hp.hpl.jena.query.Dataset;

public interface ReadTransactionCallback<T> {
	public T executeInTransaction(Dataset dataset) throws Exception;
}