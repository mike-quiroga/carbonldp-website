package com.carbonldp.repository.txn;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Vars;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.apps.context.AppContextImpl;
import com.carbonldp.repository.RepositoryService;
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
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
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
	RepositoryService repositoryService;

	private final RepositoryManager appsRepositoryManager;
	private final Map<String, RepositoryConnectionFactory> appsRepositoryConnectionFactoryMap;

	public ApplicationContextConnectionFactory( RepositoryConnectionFactory platformConnectionFactory, RepositoryManager appsRepositoryManager ) {
		this.platformConnectionFactory = platformConnectionFactory;
		this.appsRepositoryManager = appsRepositoryManager;
		this.appsRepositoryConnectionFactoryMap = new ConcurrentHashMap<>( 128 );
	}

	@Override
	public RepositoryConnection getConnection() {
		return getRepositoryConnectionFactory().getConnection();
	}

	@Override
	public void closeConnection() {
		getRepositoryConnectionFactory().closeConnection();
	}

	@Override
	public SesameTransactionObject createTransaction() throws RepositoryException {
		RepositoryConnectionFactory connectionFactory = getRepositoryConnectionFactory();

		if ( LOG.isDebugEnabled() ) {
			if ( connectionFactory == platformConnectionFactory ) LOG.debug( "getConnection << PLATFORM context" );
			else LOG.debug( "createTransaction << AppContext: {}", AppContextHolder.getContext().getApplication().toString() );
		}

		return getRepositoryConnectionFactory().createTransaction();
	}

	@Override
	public void endTransaction( boolean rollback ) throws RepositoryException {
		RepositoryConnectionFactory connectionFactory = getRepositoryConnectionFactory();

		if ( LOG.isDebugEnabled() ) {
			if ( rollback ) {
				if ( connectionFactory == platformConnectionFactory ) LOG.debug( "endTransaction << ROLLBACK - PLATFORM context" );
				else LOG.debug( "endTransaction << ROLLBACK - AppContext: {}", AppContextHolder.getContext().getApplication().toString() );
			} else {
				if ( connectionFactory == platformConnectionFactory ) LOG.debug( "endTransaction << COMMIT - PLATFORM context" );
				else LOG.debug( "endTransaction << COMMIT - AppContext: {}", AppContextHolder.getContext().getApplication().toString() );

			}
		}

		connectionFactory.endTransaction( rollback );
	}

	@Override
	public SesameTransactionObject getLocalTransactionObject() {
		return getRepositoryConnectionFactory().getLocalTransactionObject();
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
			Vars vars = Vars.getInstance();
			File file = new File( vars.getAppRespositoriesFolder() + "/" + appRepositoryID );
			if ( ! file.exists() ) throw new SesameTransactionException( "No such repository: " + appRepositoryID );
			repositoryService.createRepository( appRepositoryID );
			repository = appsRepositoryManager.getRepository( appRepositoryID );
		}

		RepositoryConnectionFactory connectionFactory = new RepositoryConnectionFactory( repository );
		appsRepositoryConnectionFactoryMap.put( appRepositoryID, connectionFactory );
	}

	@Autowired
	public void setRepositoryService( RepositoryService repositoryService ) {
		this.repositoryService = repositoryService;
	}
}
