package com.carbonldp.repository.services;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.config.RepositoryImplConfig;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.config.SailImplConfig;
import org.openrdf.sail.nativerdf.config.NativeStoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carbonldp.AbstractService;
import com.carbonldp.commons.exceptions.CarbonRuntimeException;
import com.carbonldp.repository.ReadOnlyRepositoryConnection;
import com.carbonldp.repository.ReadTransactionCallback;
import com.carbonldp.repository.ReadTransactionTemplate;
import com.carbonldp.repository.RepositoryRuntimeException;
import com.carbonldp.repository.WriteTransactionCallback;
import com.carbonldp.repository.WriteTransactionTemplate;

@Service
public class LocalRepositoryService extends AbstractService implements RepositoryService {

	@Autowired
	private RepositoryManager manager;

	@Override
	public void createRepository(String repositoryID) {
		SailImplConfig sailConfig = new NativeStoreConfig();
		RepositoryImplConfig repositoryTypeSpec = new SailRepositoryConfig(sailConfig);

		RepositoryConfig repositoryConfig = new RepositoryConfig(repositoryID, repositoryTypeSpec);

		try {
			manager.addRepositoryConfig(repositoryConfig);
		} catch (RepositoryException | RepositoryConfigException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx createRepository() > The repository: '{}', couldn't be created.", repositoryID);
			}
			throw new RepositoryRuntimeException(0x0003, e);
		}

		Repository repository = null;
		try {
			repository = manager.getRepository(repositoryID);
		} catch (RepositoryConfigException | RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx createRepository() > The repository: '{}', was created but couldn't be retrieved back.", repositoryID);
			}
			throw new RepositoryRuntimeException(0x0004, e);
		}

		try {
			repository.initialize();
		} catch (RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx createRepository() > The repository: '{}', couldn't be initialized.", repositoryID);
			}
			throw new RepositoryRuntimeException(0x0005, e);
		}
	}

	@Override
	public boolean repositoryExists(String repositoryID) {
		try {
			return manager.hasRepositoryConfig(repositoryID);
		} catch (RepositoryException | RepositoryConfigException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx repositoryExists() > There was a problem checking existance of the repository: '{}'.", repositoryID);
			}
			throw new RepositoryRuntimeException(0x0006, e);
		}
	}

	private RepositoryConnection getConnection(String repositoryID) {
		Repository repository;
		try {
			repository = manager.getRepository(repositoryID);
		} catch (RepositoryConfigException | RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx getConnection() > The repository: '{}', couldn't be retrieved.", repositoryID);
			}
			throw new RepositoryRuntimeException(0x0007, e);
		}
		RepositoryConnection connection;
		try {
			connection = repository.getConnection();
		} catch (RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx getConnection() > A connection from the repository: '{}', couldn't be retrieved.", repositoryID);
			}
			throw new RepositoryRuntimeException(0x0008, e);
		}

		return connection;
	}

	@Override
	public <T> ReadTransactionTemplate<T> getReadTransactionTemplate(String repositoryID) {
		RepositoryConnection connection = getConnection(repositoryID);
		return new LocalReadTransactionTemplate<T>(connection);
	}

	@Override
	public WriteTransactionTemplate getWriteTransactionTemplate(String repositoryID) {
		RepositoryConnection connection = getConnection(repositoryID);
		return new LocalWriteTransactionTemplate(connection);
	}

	@Override
	public void deleteRepository(String repositoryID) {
		try {
			manager.removeRepository(repositoryID);
		} catch (RepositoryException | RepositoryConfigException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx deleteRepository() > The repository: '{}', couldn't be deleted.", repositoryID);
			}
			throw new RepositoryRuntimeException(0x0009, e);
		}
	}

	private abstract class LocalTransactionTemplate {
		protected RepositoryConnection connection;

		public LocalTransactionTemplate() {
		}

		public LocalTransactionTemplate(RepositoryConnection connection) {
			this.connection = connection;
		}

		protected void beginTransaction() {
			try {
				connection.begin();
			} catch (RepositoryException e) {
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< beginTransaction > The transaction couldn't be started.");
				}
				throw new RepositoryRuntimeException(0x000A, e);
			}
		}

		protected void closeConnection() {
			try {
				connection.close();
			} catch (RepositoryException e) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx closeConnection > Exception Stacktrace:", e);
				}
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< closeConnection > The connection couldn't be closed.");
				}
			}
		}

	}

	private class LocalReadTransactionTemplate<T> extends LocalTransactionTemplate implements ReadTransactionTemplate<T> {

		public LocalReadTransactionTemplate(RepositoryConnection connection) {
			super();
			RepositoryConnection readOnlyConnection = new ReadOnlyRepositoryConnection(connection);
			this.connection = readOnlyConnection;
		}

		@Override
		public T execute(ReadTransactionCallback<T> callback) {
			try {
				return callback.executeInTransaction(connection);
			} catch (RepositoryException e) {
				throw new RepositoryRuntimeException(e);
			} catch (CarbonRuntimeException e) {
				throw e;
			} catch (Throwable e) {
				throw new CarbonRuntimeException(e);
			} finally {
				closeConnection();
			}
		}
	}

	private class LocalWriteTransactionTemplate extends LocalTransactionTemplate implements WriteTransactionTemplate {

		private List<WriteTransactionCallback> callbacks;

		public LocalWriteTransactionTemplate(RepositoryConnection connection) {
			super(connection);
			callbacks = new ArrayList<WriteTransactionCallback>();
		}

		@Override
		public void addCallback(WriteTransactionCallback callback) {
			callbacks.add(callback);
		}

		@Override
		public void execute() {
			beginTransaction();
			try {
				for (WriteTransactionCallback callback : callbacks) {
					callback.executeInTransaction(connection);
				}

				commitTransaction();
			} catch (RepositoryException e) {
				rollbackTransaction();
				throw new RepositoryRuntimeException(e);
			} catch (CarbonRuntimeException e) {
				rollbackTransaction();
				throw e;
			} catch (Throwable e) {
				rollbackTransaction();
				throw new CarbonRuntimeException(e);
			} finally {
				closeConnection();
			}
		}

		@Override
		public void execute(WriteTransactionCallback callback) {
			addCallback(callback);
			execute();
		}

		protected void rollbackTransaction() {
			try {
				connection.rollback();
			} catch (RepositoryException e) {
				if ( LOG.isErrorEnabled() ) {
					LOG.error("xx rollbackTransaction() > The transaction couldn't be rolled back.");
				}
				throw new RepositoryRuntimeException(0x000B, e);
			}
		}

		protected void commitTransaction() {
			try {
				connection.commit();
			} catch (RepositoryException e) {
				if ( LOG.isErrorEnabled() ) {
					LOG.error("xx commitTransaction() > The transaction couldn't be committed.");
				}
				throw new RepositoryRuntimeException(0x000C, e);
			}
		}
	}
}
