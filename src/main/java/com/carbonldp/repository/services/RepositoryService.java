package com.carbonldp.repository.services;

import com.carbonldp.repository.ReadTransactionTemplate;
import com.carbonldp.repository.WriteTransactionTemplate;

public interface RepositoryService {
	public void createRepository(String repositoryID);

	public boolean repositoryExists(String repositoryID);

	public <T> ReadTransactionTemplate<T> getReadTransactionTemplate(String repositoryID);

	public WriteTransactionTemplate getWriteTransactionTemplate(String repositoryID);

	public void deleteRepository(String repositoryID);
}
