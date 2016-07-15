package com.carbonldp.repository.txn;

import com.carbonldp.Vars;
import com.carbonldp.repository.LocalRepositoryService;
import com.carbonldp.repository.RepositoryService;
import com.carbonldp.repository.SpringLocalRepositoryManager;
import com.carbonldp.spring.TransactionWrapper;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.eclipse.rdf4j.repository.manager.RemoteRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.eclipse.rdf4j.repository.sail.SailRepository;
import org.eclipse.rdf4j.sail.nativerdf.SecuredNativeStore;
import org.eclipse.rdf4j.spring.RepositoryConnectionFactory;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.eclipse.rdf4j.spring.SesameTransactionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;

@Configuration
@EnableTransactionManagement
public class TxnConfig {
	@Bean
	public PlatformTransactionManager transactionManager() {
		return new SesameTransactionManager( connectionFactory() );
	}

	@Bean
	public SesameConnectionFactory connectionFactory() {
		return new ApplicationContextConnectionFactory( platformConnectionFactory(), appsRepositoryManager() );
	}

	@Bean
	public RepositoryService appRepositoryService() {
		return new LocalRepositoryService( appsRepositoryManager() );
	}

	private RepositoryConnectionFactory platformConnectionFactory() {
		return new RepositoryConnectionFactory( platformRepository() );
	}

	private Repository platformRepository() {
		String repositoryDirectory = Vars.getInstance().getPlatformRepositoryDirectory();
		SecuredNativeStore sail = new SecuredNativeStore( new File( repositoryDirectory ) );
		SailRepository platformRepository = new SailRepository( sail );

		try {
			platformRepository.initialize();
		} catch ( RepositoryException e ) {
			// TODO: Add error code
			throw new RepositoryRuntimeException( e );
		}

		return platformRepository;
	}

	@Bean
	protected RepositoryManager appsRepositoryManager() {
		if ( Vars.getInstance().appsUseRemoteManager() ) {
			String remoteManagerURL = Vars.getInstance().getAppsRemoteManagerURL();
			return new RemoteRepositoryManager( remoteManagerURL );
		} else {
			String repositoryDirectory = Vars.getInstance().getAppsRepositoryDirectory();
			return new SpringLocalRepositoryManager( new File( repositoryDirectory ) );
		}
	}

	@Bean
	protected TransactionWrapper transactionWrapper() {
		return new TransactionWrapper();
	}
}
