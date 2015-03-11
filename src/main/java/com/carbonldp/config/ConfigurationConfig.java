package com.carbonldp.config;

import com.carbonldp.spring.DependencyInjectorListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource( "classpath:${APP_ENV:local}-config.properties" )
public class ConfigurationConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Bean
	public ConfigurationRepository configurationRepository() {
		return new PropertiesFileConfigurationRepository();
	}

	@Bean
	public DependencyInjectorListener dependencyInjector() {
		return new DependencyInjectorListener();
	}
}
