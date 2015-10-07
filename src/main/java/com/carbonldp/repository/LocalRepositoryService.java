package com.carbonldp.repository;

import com.carbonldp.AbstractComponent;
import com.carbonldp.repository.security.SecuredNativeStoreConfig;
import com.carbonldp.repository.txn.*;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfig;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.config.RepositoryImplConfig;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.repository.sail.config.SailRepositoryConfig;
import org.openrdf.sail.config.SailImplConfig;

public class LocalRepositoryService extends AbstractComponent implements RepositoryService {

	private final RepositoryManager manager;

	public LocalRepositoryService( RepositoryManager manager ) {
		if ( ! manager.isInitialized() ) try {
			manager.initialize();
		} catch ( RepositoryException e ) {
			throw new RepositoryRuntimeException( 0x000D );
		}
		this.manager = manager;
	}

	@Override
	public void createRepository( String repositoryID ) {
		// TODO: Make this configurable
		SailImplConfig sailConfig = new SecuredNativeStoreConfig();
		RepositoryImplConfig repositoryTypeSpec = new SailRepositoryConfig( sailConfig );

		RepositoryConfig repositoryConfig = new RepositoryConfig( repositoryID, repositoryTypeSpec );

		try {
			manager.addRepositoryConfig( repositoryConfig );
		} catch ( RepositoryException | RepositoryConfigException e ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error( "xx createRepository() > The repository: '{}', couldn't be created.", repositoryID );
			}
			throw new RepositoryRuntimeException( 0x0003, e );
		}

		Repository repository = null;
		try {
			repository = manager.getRepository( repositoryID );
		} catch ( RepositoryConfigException | RepositoryException e ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error( "xx createRepository() > The repository: '{}', was created but couldn't be retrieved back.", repositoryID );
			}
			throw new RepositoryRuntimeException( 0x0004, e );
		}

		try {
			repository.initialize();
		} catch ( RepositoryException e ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error( "xx createRepository() > The repository: '{}', couldn't be initialized.", repositoryID );
			}
			throw new RepositoryRuntimeException( 0x0005, e );
		}
	}

	@Override
	public boolean repositoryExists( String repositoryID ) {
		try {
			return manager.hasRepositoryConfig( repositoryID );
		} catch ( RepositoryException | RepositoryConfigException e ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error( "xx repositoryExists() > There was a problem checking existance of the repository: '{}'.", repositoryID );
			}
			throw new RepositoryRuntimeException( 0x0006, e );
		}
	}

	private RepositoryConnection getConnection( String repositoryID ) {
		Repository repository;
		try {
			repository = manager.getRepository( repositoryID );
		} catch ( RepositoryConfigException | RepositoryException e ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error( "xx getConnection() > The repository: '{}', couldn't be retrieved.", repositoryID );
			}
			throw new RepositoryRuntimeException( 0x0007, e );
		}
		RepositoryConnection connection;
		try {
			connection = repository.getConnection();
		} catch ( RepositoryException e ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error( "xx getConnection() > A connection from the repository: '{}', couldn't be retrieved.", repositoryID );
			}
			throw new RepositoryRuntimeException( 0x0008, e );
		}

		return connection;
	}

	@Override
	public <T> ReadTransactionTemplate<T> getReadTransactionTemplate( String repositoryID ) {
		RepositoryConnection connection = getConnection( repositoryID );
		return new ReadTransactionTemplateImpl<>( connection );
	}

	@Override
	public WriteTransactionTemplate getWriteTransactionTemplate( String repositoryID ) {
		RepositoryConnection connection = getConnection( repositoryID );
		return new WriteTransactionTemplateImpl( connection );
	}

	@Override
	public void deleteRepository( String repositoryID ) {
		try {
			manager.removeRepository( repositoryID );
		} catch ( RepositoryException | RepositoryConfigException e ) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error( "xx deleteRepository() > The repository: '{}', couldn't be deleted.", repositoryID );
			}
			throw new RepositoryRuntimeException( 0x0009, e );
		}
	}
}
