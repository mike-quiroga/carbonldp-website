package com.carbonldp.repository.services;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.config.RepositoryImplConfig;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.repository.manager.RepositoryProvider;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.config.SailImplConfig;
import org.openrdf.sail.nativerdf.config.NativeStoreConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.carbonldp.AbstractService;
import com.carbonldp.CarbonException;
import com.carbonldp.repository.ReadOnlyRepositoryConnection;
import com.carbonldp.repository.ReadTransactionCallback;
import com.carbonldp.repository.ReadTransactionTemplate;
import com.carbonldp.repository.RepositoryRuntimeException;
import com.carbonldp.repository.WriteTransactionCallback;
import com.carbonldp.repository.WriteTransactionTemplate;

@Service
public class LocalRepositoryService extends AbstractService implements RepositoryService {
	private RepositoryManager manager;

	@Value("${repositories.directory}")
	private String directory;

	@PostConstruct
	public void init() {
		try {
			manager = RepositoryProvider.getRepositoryManager(directory);
		} catch (RepositoryConfigException | RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx init() > The manager of the directory: '{}', couldn't be retrieved.", directory);
			}
			// TODO: FT
			throw new RepositoryRuntimeException(".", e);
		}
	}

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
			// TODO: FT
			throw new RepositoryRuntimeException(".", e);
		}

		Repository repository = null;
		try {
			repository = manager.getRepository(repositoryID);
		} catch (RepositoryConfigException | RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx createRepository() > The repository: '{}', was created but couldn't be retrieved back.", repositoryID);
			}
			// TODO: FT
			throw new RepositoryRuntimeException(".", e);
		}

		try {
			repository.initialize();
		} catch (RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx createRepository() > The repository: '{}', couldn't be initialized.", repositoryID);
			}
			// TODO: FT
			throw new RepositoryRuntimeException(".", e);
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
			// TODO: FT
			throw new RepositoryRuntimeException(".", e);
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
			// TODO: FT
			throw new RepositoryRuntimeException(".", e);
		}
		RepositoryConnection connection;
		try {
			connection = repository.getConnection();
		} catch (RepositoryException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("xx getConnection() > A connection from the repository: '{}', couldn't be retrieved.", repositoryID);
			}
			// TODO: FT
			throw new RepositoryRuntimeException(".", e);
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
			// TODO: FT
			throw new RepositoryRuntimeException(".", e);
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
				// TODO: FT
				throw new RepositoryRuntimeException(".", e);
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
			} catch (CarbonException e) {
				throw e;
			} catch (Throwable e) {
				throw new CarbonException(e);
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
			} catch (CarbonException e) {
				rollbackTransaction();
				throw e;
			} catch (Throwable e) {
				rollbackTransaction();
				throw new CarbonException(e);
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
				// TODO: FT
				throw new RepositoryRuntimeException(".", e);
			}
		}

		protected void commitTransaction() {
			try {
				connection.commit();
			} catch (RepositoryException e) {
				if ( LOG.isErrorEnabled() ) {
					LOG.error("xx commitTransaction() > The transaction couldn't be commited.");
				}
				// TODO: FT
				throw new RepositoryRuntimeException(".", e);
			}
		}
	}
}
