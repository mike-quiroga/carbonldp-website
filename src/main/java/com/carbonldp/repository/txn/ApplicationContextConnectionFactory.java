package com.carbonldp.repository.txn;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Vars;
import com.carbonldp.apps.context.AppContext;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.apps.context.AppContextImpl;
import com.carbonldp.repository.RepositoryService;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.config.RepositoryConfigException;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.spring.RepositoryConnectionFactory;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.eclipse.rdf4j.spring.SesameTransactionException;
import org.eclipse.rdf4j.spring.SesameTransactionObject;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 * Handles connections to multiple {@link org.eclipse.rdf4j.repository.Repository}s. Depending on the {@link AppContextHolder}
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

		// TODO: Find the real cause and fix it instead of patching it (https://jira.base22.com/browse/LDP-692)
		if ( repository == null ) repository = registerRepository( appRepositoryID );
		if ( repository == null ) throw new SesameTransactionException( "No such repository: " + appRepositoryID );

		RepositoryConnectionFactory connectionFactory = new RepositoryConnectionFactory( repository );
		appsRepositoryConnectionFactoryMap.put( appRepositoryID, connectionFactory );
	}

	private Repository registerRepository( String appRepositoryID ) {
		Vars vars = Vars.getInstance();
		File file = new File( vars.getAppRespositoriesFolder() + "/" + appRepositoryID );
		if ( ! file.exists() ) throw new SesameTransactionException( "No such repository: " + appRepositoryID );
		repositoryService.createRepository( appRepositoryID );
		return appsRepositoryManager.getRepository( appRepositoryID );
	}

	@Autowired
	public void setRepositoryService( RepositoryService repositoryService ) {
		this.repositoryService = repositoryService;
	}
}
