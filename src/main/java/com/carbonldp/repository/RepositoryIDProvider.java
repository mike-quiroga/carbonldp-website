package com.carbonldp.repository;

import org.openrdf.spring.DynamicRepositoryManagerConnectionFactory.RepositoryIdProvider;

public class RepositoryIDProvider implements RepositoryIdProvider {

	private String repositoryID;

	@Override
	public String getRepositoryId() {
		return repositoryID;
	}

	public void setRepositoryID(String repositoryID) {
		this.repositoryID = repositoryID;
	}

}