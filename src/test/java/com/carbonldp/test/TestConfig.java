package com.carbonldp.test;

import com.carbonldp.apps.context.AppContextConfig;
import com.carbonldp.config.RepositoriesConfig;
import com.carbonldp.config.ServicesConfig;
import com.carbonldp.jobs.JobConfig;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.mail.MailConfig;
import com.carbonldp.repository.txn.TxnConfig;
import com.carbonldp.security.SecurityConfig;
import com.carbonldp.test.authorization.RunWithAnnotatedServiceMock;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import( {
	TxnConfig.class,
	ConfigurationConfig.class,
	RepositoriesConfig.class,
	AppContextConfig.class,
	SecurityConfig.class,
	ServicesConfig.class,
	MailConfig.class,
	JobConfig.class
} )
public class TestConfig {
	@Autowired
	private SesameConnectionFactory connectionFactory;

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

	@Bean
	public RunWithAnnotatedServiceMock runWithAnnotatedServiceMock() {
		return new RunWithAnnotatedServiceMock();
	}

}
