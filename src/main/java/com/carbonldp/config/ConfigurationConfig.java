package com.carbonldp.config;

import com.carbonldp.Application;
import com.carbonldp.spring.DependencyInjectorListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
public class ConfigurationConfig {
	public static class Config {
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
			configurer.setProperties( Application.getInstance().getConfiguration() );
			return configurer;
		}
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
