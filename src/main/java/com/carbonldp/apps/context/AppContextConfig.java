package com.carbonldp.apps.context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import com.carbonldp.apps.AppService;

@Configuration
@EnableAspectJAutoProxy
public class AppContextConfig {

	@Autowired
	private AppService appService;

	@Bean
	public AppContextPersistanceFilter appContextPersistanceFilter() {
		return new AppContextPersistanceFilter(appContextRepository());
	}

	@Bean
	public AppContextRepository appContextRepository() {
		return new AppContextRepository(appService);
	}

	@Bean
	public AppContextExchanger appContextExchanger() {
		return new AppContextExchanger();
	}
}