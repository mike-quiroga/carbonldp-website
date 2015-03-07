package com.carbonldp.test;

import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
	Transactions transactions() {
		return new Transactions(connectionFactory);
	}
}
