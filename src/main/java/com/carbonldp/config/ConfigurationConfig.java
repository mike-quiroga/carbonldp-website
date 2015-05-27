package com.carbonldp.config;

import com.carbonldp.spring.DependencyInjectorListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class ConfigurationConfig {

	private static final Resource[] LOCAL_PROPERTIES = new ClassPathResource[]{
		new ClassPathResource( "local-config.properties" ),
	};
	private static final Resource[] DEV_PROPERTIES = new ClassPathResource[]{
		new ClassPathResource( "dev-config.properties" ),
	};

	@Profile( "local" )
	public static class LocalConfig {
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
			configurer.setLocations( LOCAL_PROPERTIES );
			return configurer;
		}
	}

	@Profile( "dev" )
	public static class DevConfig {
		@Bean
		public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
			configurer.setLocations( DEV_PROPERTIES );
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
