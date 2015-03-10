package com.carbonldp;

import com.carbonldp.apps.context.AppContextConfig;
import com.carbonldp.config.ConfigurationConfig;
import com.carbonldp.config.RepositoriesConfig;
import com.carbonldp.config.ServicesConfig;
import com.carbonldp.repository.txn.TxnConfig;
import com.carbonldp.security.SecurityConfig;
import com.carbonldp.utils.PropertiesUtil;
import com.carbonldp.web.WebConfig;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.Log4jConfigListener;

import javax.servlet.*;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Properties;

import static com.carbonldp.Consts.COMMA;

public class ApplicationInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup( ServletContext container ) throws ServletException {

		addLog4jListener( container );

		SpringProfile activeProfile = getActiveProfile();
		Properties configProperties = loadConfigProperties( activeProfile );
		loadGlobalVars( configProperties );

		AnnotationConfigWebApplicationContext rootContext = createRootContext();
		addContextLifecycleManagerListener( rootContext, container );

		addSecurityFilterChain( container );

		AnnotationConfigWebApplicationContext dispatcherContext = createDispatcherContext();
		ServletRegistration.Dynamic dispatcher = registerDispatcherServlet( dispatcherContext, container );

		setMultipartConfig( dispatcher );
	}

	private SpringProfile getActiveProfile() {
		String springProfiles = System.getenv( "Dspring.profiles.active" );
		String[] profiles = springProfiles.split( COMMA );
		for ( String profile : profiles ) {
			SpringProfile springProfile = SpringProfile.findByName( profile.trim() );
			if ( springProfile != null ) return springProfile;
		}
		return SpringProfile.DEFAULT;
	}

	private Properties loadConfigProperties( SpringProfile activeProfile ) throws ServletException {
		Resource propertiesFile = new ClassPathResource( activeProfile.getName() + "-config.properties" );
		PropertiesFactoryBean factory = new PropertiesFactoryBean();
		factory.setLocation( propertiesFile );

		Properties properties;
		try {
			factory.afterPropertiesSet();
			properties = factory.getObject();
		} catch ( IOException e ) {
			throw new ServletException( "Couldn't load the config properties file.", e );
		}

		PropertiesUtil.resolveProperties( properties );

		return properties;
	}

	private void loadGlobalVars( Properties properties ) {
		Vars.init( properties );
	}

	private void addLog4jListener( ServletContext container ) {
		container.setInitParameter( "log4jConfigLocation", "classpath:log4j.properties" );
		container.addListener( new Log4jConfigListener() );
	}

	private AnnotationConfigWebApplicationContext createRootContext() {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(
			TxnConfig.class,
			ConfigurationConfig.class,
			RepositoriesConfig.class,
			ServicesConfig.class,
			AppContextConfig.class,
			SecurityConfig.class
		);
		return rootContext;
	}

	private void addContextLifecycleManagerListener( WebApplicationContext context, ServletContext container ) {
		container.addListener( new ContextLoaderListener( context ) );
	}

	private void addSecurityFilterChain( ServletContext container ) {
		FilterRegistration.Dynamic securityFilter = container.addFilter( AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, new DelegatingFilterProxy( AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME ) );
		securityFilter.addMappingForUrlPatterns( EnumSet.allOf( DispatcherType.class ), false, "/*" );
	}

	private AnnotationConfigWebApplicationContext createDispatcherContext() {
		AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
		dispatcherContext.register(
			WebConfig.class
		);
		return dispatcherContext;
	}

	private ServletRegistration.Dynamic registerDispatcherServlet( AnnotationConfigWebApplicationContext dispatcherContext, ServletContext container ) {
		ServletRegistration.Dynamic dispatcher = container.addServlet( "dispatcher", new DispatcherServlet( dispatcherContext ) );
		dispatcher.setLoadOnStartup( 1 );
		dispatcher.addMapping( "/" );
		return dispatcher;
	}

	private void setMultipartConfig( ServletRegistration.Dynamic dispatcher ) {
		// TODO: When implementing multipart, verify these settings
		dispatcher.setMultipartConfig( new MultipartConfigElement( "/tmp", 1024 * 1024 * 5, 1024 * 1024 * 5 * 5, 1024 * 1024 ) );
	}

}
