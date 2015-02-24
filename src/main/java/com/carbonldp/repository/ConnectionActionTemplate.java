package com.carbonldp.repository;

import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.repository.txn.RepositoryRuntimeException;

public class ConnectionActionTemplate {
	private final SesameConnectionFactory connectionFactory;

	public ConnectionActionTemplate(SesameConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public <E> E execute(ConnectionActionCallback<E> callback) {
		RepositoryConnection connection = connectionFactory.getConnection();
		try {
			return callback.doWithConnection(connection);
		} catch (RepositoryException e) {
			throw new RepositoryRuntimeException(e);
		}
	}

	public void execute(EmptyConnectionActionCallback callback) {
		RepositoryConnection connection = connectionFactory.getConnection();
		try {
			callback.doWithConnection(connection);
		} catch (RepositoryException e) {
			throw new RepositoryRuntimeException(e);
		}
	}
}
