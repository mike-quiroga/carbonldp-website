package com.carbonldp.repository;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public interface ConnectionActionCallback<E> {
	public E doWithConnection(RepositoryConnection connection) throws RepositoryException;
}
