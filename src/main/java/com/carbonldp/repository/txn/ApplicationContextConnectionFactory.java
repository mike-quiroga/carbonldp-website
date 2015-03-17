package com.carbonldp.repository.txn;

import com.carbonldp.AbstractComponent;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.apps.context.AppContextImpl;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.config.RepositoryConfigException;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.spring.RepositoryConnectionFactory;
import org.openrdf.spring.SesameConnectionFactory;
import org.openrdf.spring.SesameTransactionException;
import org.openrdf.spring.SesameTransactionObject;
import org.springframework.beans.factory.DisposableBean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Handles connections to multiple {@link org.openrdf.repository.Repository}s. Depending on the {@link AppContextHolder}
 * , it can manage connections from the platform repository or the applications repositories manager.
 * </p>
 * <p/>
 * <p>
 * <b>Caution!</b> When changing the {@link AppContextImpl} in the {@link AppContextHolder} while in a transaction, be
 * sure to return to the previous {@link AppContextImpl} before the transactional method finishes so the transaction
 * ends properly.
 * </p>
 *
 * @author MiguelAraCo
 */
public class ApplicationContextConnectionFactory extends AbstractComponent implements SesameConnectionFactory, DisposableBean {

	private final RepositoryConnectionFactory platformConnectionFactory;

	private final RepositoryManager appsRepositoryManager;
	private final Map<String, RepositoryConnectionFactory> appsRepositoryConnectionFactoryMap;

	public ApplicationContextConnectionFactory( RepositoryConnectionFactory platformConnectionFactory, RepositoryManager appsRepositoryManager ) {
		this.platformConnectionFactory = platformConnectionFactory;
		this.appsRepositoryManager = appsRepositoryManager;
		this.appsRepositoryConnectionFactoryMap = new ConcurrentHashMap<>( 128 );
	}

	@Override
	public RepositoryConnection getConnection() {
		RepositoryConnectionFactory connectionFactory = getRepositoryConnectionFactory();

		if ( LOG.isDebugEnabled() ) {
			if ( connectionFactory == platformConnectionFactory ) LOG.debug( "getConnection << PLATFORM context" );
			else LOG.debug( "getConnection << AppContext: {}", AppContextHolder.getContext().getApplication() );
		}

		return connectionFactory.getConnection();
	}

	@Override
	public void closeConnection() {
		RepositoryConnectionFactory connectionFactory = getRepositoryConnectionFactory();

		if ( LOG.isDebugEnabled() ) {
			if ( connectionFactory == platformConnectionFactory ) LOG.debug( "getConnection << PLATFORM context" );
			else LOG.debug( "closeConnection << AppContext: {}", AppContextHolder.getContext().getApplication() );
		}

		connectionFactory.closeConnection();
	}

	@Override
	public SesameTransactionObject createTransaction() throws RepositoryException {
		RepositoryConnectionFactory connectionFactory = getRepositoryConnectionFactory();

		if ( LOG.isDebugEnabled() ) {
			if ( connectionFactory == platformConnectionFactory ) LOG.debug( "getConnection << PLATFORM context" );
			else LOG.debug( "createTransaction << AppContext: {}", AppContextHolder.getContext().getApplication() );
		}

		return connectionFactory.createTransaction();
	}

	@Override
	public void endTransaction( boolean rollback ) throws RepositoryException {
		RepositoryConnectionFactory connectionFactory = getRepositoryConnectionFactory();

		if ( LOG.isDebugEnabled() ) {
			if ( rollback ) {
				if ( connectionFactory == platformConnectionFactory ) LOG.debug( "endTransaction << ROLLBACK - PLATFORM context" );
				else LOG.debug( "endTransaction << ROLLBACK - AppContext: {}", AppContextHolder.getContext().getApplication() );
			} else {
				if ( connectionFactory == platformConnectionFactory ) LOG.debug( "endTransaction << COMMIT - PLATFORM context" );
				else LOG.debug( "endTransaction << COMMIT - AppContext: {}", AppContextHolder.getContext().getApplication() );

			}
		}

		connectionFactory.endTransaction( rollback );
	}

	@Override
	public SesameTransactionObject getLocalTransactionObject() {
		RepositoryConnectionFactory connectionFactory = getRepositoryConnectionFactory();

		if ( LOG.isDebugEnabled() ) {
			if ( connectionFactory == platformConnectionFactory ) LOG.debug( "getLocalTransactionObject << PLATFORM context" );
			else LOG.debug( "getLocalTransactionObject << AppContext: {}", AppContextHolder.getContext().getApplication() );
		}

		return connectionFactory.getLocalTransactionObject();
	}

	@Override
	public void destroy() throws Exception {
		destroyPlatformRepositoryConnectionFactory();
		destroyAppsRepositoryConnectionFactories();
	}

	private void destroyPlatformRepositoryConnectionFactory() throws Exception {
		platformConnectionFactory.destroy();
	}

	private void destroyAppsRepositoryConnectionFactories() throws Exception {
		for ( RepositoryConnectionFactory appRepositoryConnectionFactory : appsRepositoryConnectionFactoryMap.values() ) {
			appRepositoryConnectionFactory.destroy();
		}

		appsRepositoryConnectionFactoryMap.clear();
	}

	private RepositoryConnectionFactory getRepositoryConnectionFactory() {
		AppContext context = AppContextHolder.getContext();

		if ( context.isEmpty() ) return platformConnectionFactory;

		String appRepositoryID = context.getApplication().getRepositoryID();

		if ( ! appsRepositoryConnectionFactoryMap.containsKey( appRepositoryID ) ) initializeAppRepositoryConnectionFactory( appRepositoryID );

		return appsRepositoryConnectionFactoryMap.get( appRepositoryID );
	}

	private void initializeAppRepositoryConnectionFactory( String appRepositoryID ) {
		Repository repository;

		try {
			repository = appsRepositoryManager.getRepository( appRepositoryID );
		} catch ( RepositoryConfigException | RepositoryException e ) {
			throw new SesameTransactionException( e );
		}

		if ( repository == null ) {
			// TODO: Add error code
			throw new SesameTransactionException( "No such repository: " + appRepositoryID );
		}

		RepositoryConnectionFactory connectionFactory = new RepositoryConnectionFactory( repository );
		appsRepositoryConnectionFactoryMap.put( appRepositoryID, connectionFactory );
	}
}
