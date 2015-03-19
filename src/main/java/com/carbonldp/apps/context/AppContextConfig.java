package com.carbonldp.apps.context;

import com.carbonldp.apps.AppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.util.Assert;

@Configuration
@EnableAspectJAutoProxy
public class AppContextConfig {

	@Autowired
	public AppRepository appRepository;

	@Bean
	public AppContextPersistenceFilter appContextPersistanceFilter() {
		return new AppContextPersistenceFilter( appContextRepository() );
	}

	@Bean
	public AppContextRepository appContextRepository() {
		Assert.notNull( appRepository );
		return new AppContextRepository( appRepository );
	}

	@Bean
	public AppContextExchanger appContextExchanger() {
		return new AppContextExchanger();
	}
}