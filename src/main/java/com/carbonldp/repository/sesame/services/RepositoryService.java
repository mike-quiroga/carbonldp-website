package com.carbonldp.repository.sesame.services;

import com.carbonldp.repository.sesame.ReadTransactionTemplate;
import com.carbonldp.repository.sesame.WriteTransactionTemplate;

public interface RepositoryService {
	public void createRepository(String repositoryID);

	public boolean repositoryExists(String repositoryID);

	public <T> ReadTransactionTemplate<T> getReadTransactionTemplate(String repositoryID);

	public WriteTransactionTemplate getWriteTransactionTemplate(String repositoryID);

	public void deleteRepository(String repositoryID);
}
