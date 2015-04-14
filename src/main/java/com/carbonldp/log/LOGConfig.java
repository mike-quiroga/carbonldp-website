package com.carbonldp.log;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class LOGConfig {
	@Bean
	public ServiceCallsLogger serviceCallsLogger() {
		return new ServiceCallsLogger();
	}

	@Bean
	public RepositoryCallsLogger repositoryCallsLogger() {
		return new RepositoryCallsLogger();
	}
}
