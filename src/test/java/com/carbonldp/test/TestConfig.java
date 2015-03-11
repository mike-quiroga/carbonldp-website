package com.carbonldp.test;

import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.carbonldp.apps.context.AppContextConfig;
import com.carbonldp.ldp.services.ContainerService;
import com.carbonldp.repository.RepositoryConfig;
import com.carbonldp.security.SecurityConfig;

@Configuration
//@formatter:off
@Import({ 
		SecurityConfig.class,
		ConfigurationConfig.class,
		AppContextConfig.class,
		RepositoryConfig.class 
})
//@formatter:on
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
		return new TransactionActionTemplate(connectionFactory);
	}

}
