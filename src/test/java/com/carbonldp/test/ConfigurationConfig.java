package com.carbonldp.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import com.carbonldp.ConfigurationRepository;
import com.carbonldp.PropertiesFileConfigurationRepository;
import com.carbonldp.spring.DependencyInjectorListener;

@Configuration
@PropertySource({ "classpath:${APP_ENV:local}-config.properties", "classpath:config.properties" })
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
