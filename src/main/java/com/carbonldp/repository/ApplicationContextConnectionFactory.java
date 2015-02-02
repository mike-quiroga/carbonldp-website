package com.carbonldp.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

import com.carbonldp.apps.context.ApplicationContext;
import com.carbonldp.apps.context.ApplicationContextHolder;
import com.carbonldp.apps.context.ApplicationContextImpl;

/**
 * <p>
 * Handles connections to multiple {@link org.openrdf.repository.Repository}s. Depending on the
 * {@link ApplicationContextHolder}, it can manage connections from the platform repository or the applications
 * repositories manager.
 * </p>
 * <p/>
 * <p>
 * <b>Caution!</b> When changing the {@link ApplicationContextImpl} in the {@link ApplicationContextHolder} while in a
 * transaction, be sure to return to the previous {@link ApplicationContextImpl} before the transactional method
 * finishes so the transaction ends properly.
 * </p>
 * 
 * @author MiguelAraCo
 *
 */
public class ApplicationContextConnectionFactory implements SesameConnectionFactory, DisposableBean {

	private final RepositoryConnectionFactory platformConnectionFactory;

	private final RepositoryManager appsRepositoryManager;
	private final Map<String, RepositoryConnectionFactory> appsRepositoryConnectionFactoryMap;

	public ApplicationContextConnectionFactory(RepositoryConnectionFactory platformConnectionFactory, RepositoryManager appsRepositoryManager) {
		this.platformConnectionFactory = platformConnectionFactory;
		this.appsRepositoryManager = appsRepositoryManager;
		this.appsRepositoryConnectionFactoryMap = new ConcurrentHashMap<String, RepositoryConnectionFactory>(128);
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
		return getRepositoryConnectionFactory().createTransaction();
	}

	@Override
	public void endTransaction(boolean rollback) throws RepositoryException {
		getRepositoryConnectionFactory().endTransaction(rollback);
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
		for (RepositoryConnectionFactory appRepositoryConnectionFactory : appsRepositoryConnectionFactoryMap.values()) {
			appRepositoryConnectionFactory.destroy();
		}

		appsRepositoryConnectionFactoryMap.clear();
	}

	private RepositoryConnectionFactory getRepositoryConnectionFactory() {
		ApplicationContext context = ApplicationContextHolder.getContext();

		if ( context.isEmpty() ) return platformConnectionFactory;

		String appRepositoryID = context.getApplication().getRepositoryID();

		if ( ! appsRepositoryConnectionFactoryMap.containsKey(appRepositoryID) ) initializeAppRepositoryConnectionFactory(appRepositoryID);

		RepositoryConnectionFactory repositoryConnectionFactory = appsRepositoryConnectionFactoryMap.get(appRepositoryID);

		return repositoryConnectionFactory;
	}

	private void initializeAppRepositoryConnectionFactory(String appRepositoryID) {
		Repository repository;

		try {
			repository = appsRepositoryManager.getRepository(appRepositoryID);
		} catch (RepositoryConfigException | RepositoryException e) {
			throw new SesameTransactionException(e);
		}

		if ( repository == null ) {
			// TODO: Add error code
			throw new SesameTransactionException("No such repository: " + appRepositoryID);
		}

		RepositoryConnectionFactory connectionFactory = new RepositoryConnectionFactory(repository);
		appsRepositoryConnectionFactoryMap.put(appRepositoryID, connectionFactory);
	}
}
