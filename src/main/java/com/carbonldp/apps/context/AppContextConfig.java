package com.carbonldp.apps.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
public class AppContextConfig {

	@Bean
	public AppContextPersistanceFilter appContextPersistanceFilter() {
		return new AppContextPersistanceFilter(appContextRepository());
	}

	@Bean
	public AppContextRepository appContextRepository() {
		return new AppContextRepository();
	}

	@Bean
	public AppContextExchanger appContextExchanger() {
		return new AppContextExchanger();
	}
}