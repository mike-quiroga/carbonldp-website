package com.carbonldp.repository;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public interface EmptyConnectionActionCallback {
	public void doWithConnection(RepositoryConnection connection) throws RepositoryException;
}
