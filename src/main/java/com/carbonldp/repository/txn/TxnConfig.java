package com.carbonldp.repository.txn;

import java.io.File;

import org.openrdf.repository.Repository;
import org.openrdf.repository.manager.LocalRepositoryManager;
import org.openrdf.repository.manager.RepositoryManager;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.nativerdf.NativeStore;
import org.openrdf.spring.RepositoryConnectionFactory;
import org.openrdf.spring.SesameConnectionFactory;
import org.openrdf.spring.SesameTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.carbonldp.ConfigurationRepository;

@Configuration
@ComponentScan("org.openrdf.spring")
@EnableTransactionManagement
public class TxnConfig {
	@Autowired
	private ConfigurationRepository configurationRepository;

	@Bean
	public PlatformTransactionManager transactionManager() {
		return new SesameTransactionManager(connectionFactory());
	}

	@Bean
	public SesameConnectionFactory connectionFactory() {
		return new ApplicationContextConnectionFactory(platformConnectionFactory(), appsRepositoryManager());
	}

	private RepositoryConnectionFactory platformConnectionFactory() {
		return new RepositoryConnectionFactory(platformRepository());
	}

	private Repository platformRepository() {
		String repositoryDirectory = configurationRepository.getPlatformRepositoryDirectory();
		NativeStore platformConfig = new NativeStore(new File(repositoryDirectory));
		SailRepository platformRepository = new SailRepository(platformConfig);
		return platformRepository;
	}

	private RepositoryManager appsRepositoryManager() {
		String repositoryDirectory = configurationRepository.getAppsRepositoryDirectory();
		return new LocalRepositoryManager(new File(repositoryDirectory));
	}
}
