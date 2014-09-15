package com.base22.carbon.repository.services;

import com.base22.carbon.CarbonException;
import com.base22.carbon.repository.ReadTransactionTemplate;
import com.base22.carbon.repository.RepositoryServiceException;
import com.base22.carbon.repository.WriteTransactionTemplate;

public interface RepositoryService {

	public void release() throws RepositoryServiceException;

	public void createDataset(String datasetName) throws RepositoryServiceException;

	public boolean datasetExists(String dataset) throws RepositoryServiceException;

	public <T> ReadTransactionTemplate<T> getReadTransactionTemplate(String datasetName) throws CarbonException;

	public WriteTransactionTemplate getWriteTransactionTemplate(String datasetName) throws CarbonException;
}
