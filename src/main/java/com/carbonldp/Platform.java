package com.carbonldp;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.beust.jcommander.converters.FileConverter;
import com.carbonldp.apps.context.AppContextConfig;
import com.carbonldp.authorization.acl.ACLWiringConfig;
import com.carbonldp.cache.CacheConfig;
import com.carbonldp.config.ConfigurationConfig;
import com.carbonldp.config.RepositoriesConfig;
import com.carbonldp.config.ServicesConfig;
import com.carbonldp.jobs.JobConfig;
import com.carbonldp.log.LOGConfig;
import com.carbonldp.log.RequestLoggerFilter;
import com.carbonldp.mail.MailConfig;
import com.carbonldp.repository.RepositoriesUpdater;
import com.carbonldp.repository.security.SecuredNativeStoreFactory;
import com.carbonldp.repository.txn.TxnConfig;
import com.carbonldp.security.SecurityConfig;
import com.carbonldp.utils.PropertiesUtil;
import com.carbonldp.web.config.WebConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.rdf4j.sail.config.SailRegistry;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * @author MiguelAraCo
 * @since 0.23.0-ALPHA
 */
public class Platform {
	private static final String DEFAULT_CONFIG_FILE_NAME = "config.properties";
	private static final File defaultConfigurationFile = new File( "/opt/carbon/config/" + DEFAULT_CONFIG_FILE_NAME );

	private static Platform instance = null;

	public static void main( String[] args ) throws Exception {
		instance = new Platform();
		instance.start( args );
	}

	public static Platform getInstance() {
		return instance;
	}

	private void start( String[] args ) throws Exception {
		Arguments arguments = parseArguments( args );

		Properties configProperties = loadConfiguration( arguments.configurationFile );
		Application.getInstance().setConfiguration( configProperties );

		setSpringActiveProfiles( arguments );

		Vars.initialize( configProperties );

		Properties errorCodes = loadErrorCodes();
		ErrorCodes.populate( errorCodes );

		SailRegistry.getInstance().add( new SecuredNativeStoreFactory() );

		RepositoriesUpdater repositoriesUpdater = new RepositoriesUpdater();
		if ( ! repositoriesUpdater.repositoriesAreUpToDate() ) {
			repositoriesUpdater.updateRepositories();
		}

		Server server = new Server( arguments.port );

		WebAppContext contextHandler = new WebAppContext();
		contextHandler.setErrorHandler( null );
		contextHandler.setContextPath( "/" );
		contextHandler.setResourceBase( "." );
		contextHandler.setConfigurations( new Configuration[]{} );

		AnnotationConfigWebApplicationContext rootContext = createRootContext();

		contextHandler.addEventListener( new ServletContextLoaderListener( rootContext ) {
			@Override
			public void contextInitialized( ServletContextEvent event ) {
				ContextHandler.Context servletContext = (ContextHandler.Context) event.getServletContext();

				servletContext.setExtendedListenerTypes( true );

				super.contextInitialized( event );

				addLoggingFilter( servletContext );
				addRequestContextFilter( servletContext );
				addSecurityFilterChain( servletContext );

				AnnotationConfigWebApplicationContext dispatcherContext = createDispatcherContext();

				ServletRegistration.Dynamic dynamic = registerDispatcherServlet( dispatcherContext, servletContext );
				setMultipartConfig( dynamic );
			}

			@Override
			public void contextDestroyed( ServletContextEvent event ) {

			}
		} );

		server.setAttribute( "requestHeaderSize", Vars.getInstance().getRequestHeaderSize() );
		server.setAttribute( "responseHeaderSize", Vars.getInstance().getResponseHeaderSize() );
		server.setHandler( contextHandler );
		server.start();
		server.join();
	}

	private Arguments parseArguments( String[] args ) {
		Arguments arguments = new Arguments();
		new JCommander( arguments, args );
		return arguments;
	}

	private Properties loadConfiguration( File configurationFile ) throws ServletException {
		Resource defaultConfiguration = new ClassPathResource( "config.properties" );

		Resource configuration = new PathResource( configurationFile.toPath() );

		if ( ! configurationFile.exists() ) {
			if ( configurationFile.getAbsolutePath().equals( Platform.defaultConfigurationFile.getAbsolutePath() ) ) {
				throw new ServletException( "The configuration file couldn't be found, please create the configuration file /opt/carbon/config/config.properties or specify another file with --config" );
			} else {
				throw new ServletException( "The configuration file specified '" + configurationFile.getAbsolutePath() + "' doesn't exist" );
			}
		} else if ( configurationFile.isDirectory() ) {
			return loadConfiguration( new File( configurationFile.getAbsolutePath() + Consts.SLASH + DEFAULT_CONFIG_FILE_NAME ) );
		} else if ( ! configurationFile.canRead() ) {
			throw new ServletException( "The configuration file '" + configurationFile.getAbsolutePath() + "' can't be read" );
		}

		Properties properties = readProperties( defaultConfiguration, configuration );

		return PropertiesUtil.resolveProperties( properties );
	}

	private Properties loadErrorCodes() throws ServletException {
		Resource propertiesFile = new ClassPathResource( "error-codes.properties" );

		return readProperties( propertiesFile );
	}

	private Properties readProperties( Resource... sources ) throws ServletException {
		PropertiesFactoryBean factory = new PropertiesFactoryBean();
		factory.setLocations( sources );

		try {
			factory.afterPropertiesSet();
			return factory.getObject();
		} catch ( IOException e ) {
			throw new ServletException( "Couldn't load the error codes file.", e );
		}
	}

	private AnnotationConfigWebApplicationContext createRootContext() {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(
			TxnConfig.class,
			ConfigurationConfig.class,
			RepositoriesConfig.class,
			AppContextConfig.class,
			CacheConfig.class,
			SecurityConfig.class,
			ACLWiringConfig.class,
			ServicesConfig.class,
			MailConfig.class,
			LOGConfig.class,
			JobConfig.class
		);
		return rootContext;
	}

	private void addRequestContextFilter( ServletContext container ) {
		FilterRegistration.Dynamic filter = container.addFilter( "REQUEST_CONTEXT_FILTER", new RequestContextFilter() );
		filter.addMappingForUrlPatterns( EnumSet.allOf( DispatcherType.class ), false, "/*" );
	}

	private void addLoggingFilter( ServletContext container ) {
		FilterRegistration.Dynamic loggerFilter = container.addFilter( RequestLoggerFilter.DEFAULT_FILTER_NAME, new RequestLoggerFilter() );
		loggerFilter.addMappingForUrlPatterns( EnumSet.allOf( DispatcherType.class ), false, "/*" );
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

	private void setSpringActiveProfiles( Arguments arguments ) {
		String activeProfilesPassed = arguments.activeProfiles.stream().collect( Collectors.joining( Consts.COMMA ) );
		String defaultProfiles = new Arguments().activeProfiles.stream().collect( Collectors.joining( Consts.COMMA ) );

		if ( ! activeProfilesPassed.equals( defaultProfiles ) || ( System.getProperty( "spring.profiles.active" ) == null && System.getenv( "SPRING_PROFILES_ACTIVE" ) == null ) ) {
			System.setProperty( "spring.profiles.active", activeProfilesPassed );
		}
	}

	private ServletRegistration.Dynamic registerDispatcherServlet( AnnotationConfigWebApplicationContext dispatcherContext, ServletContext container ) {
		ServletRegistration.Dynamic dispatcher = container.addServlet( "dispatcher", new DispatcherServlet( dispatcherContext ) );
		dispatcher.setLoadOnStartup( 1 );
		dispatcher.addMapping( "/" );
		dispatcher.setInitParameter( "dispatchOptionsRequest", "true" );
		return dispatcher;
	}

	private void setMultipartConfig( ServletRegistration.Dynamic dispatcher ) {
		// TODO: When implementing multipart, verify these settings
		dispatcher.setMultipartConfig( new MultipartConfigElement( "/tmp", 1024 * 1024 * 5, 1024 * 1024 * 5 * 5, 1024 * 1024 ) );
	}

	// This class makes ContextLoaderListener compatible with Jetty by implementing javax.servlet.ServletContextListener
	private class ServletContextLoaderListener extends ContextLoaderListener implements ServletContextListener {
		public ServletContextLoaderListener( WebApplicationContext context ) {
			super( context );
		}
	}

	public class Arguments {
		@Parameter( names = {"--port", "-p"}, description = "Port to listen to" )
		private Integer port = 8083;

		@Parameter( names = "--profiles", splitter = CommaParameterSplitter.class )
		private List<String> activeProfiles = Arrays.asList( "local" );

		@Parameter( names = "--config", converter = FileConverter.class )
		private File configurationFile = Platform.defaultConfigurationFile;
	}
}
