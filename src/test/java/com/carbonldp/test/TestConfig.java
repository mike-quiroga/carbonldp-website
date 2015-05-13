package com.carbonldp.test;

import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.carbonldp.apps.context.AppContextConfig;
import com.carbonldp.config.ConfigurationConfig;
import com.carbonldp.config.RepositoriesConfig;
import com.carbonldp.config.ServicesConfig;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.repository.txn.TxnConfig;
import com.carbonldp.security.SecurityConfig;

@Configuration
@Import( {
	TxnConfig.class,
	ConfigurationConfig.class,
	RepositoriesConfig.class,
	AppContextConfig.class,
	SecurityConfig.class,
	ServicesConfig.class
} )
public class TestConfig {
	@Autowired
	private SesameConnectionFactory connectionFactory;

	@Autowired
	private ContainerService containerService;

	@Bean
	public PlatformContextActionTemplate platformContextTemplate() {
		return new PlatformContextActionTemplate();
	}

	@Bean
	public ApplicationContextActionTemplate applicationContextTemplate() {
		return new ApplicationContextActionTemplate();
	}

	@Bean
	public TransactionActionTemplate transactions() {
		return new TransactionActionTemplate( connectionFactory );
	}

}
