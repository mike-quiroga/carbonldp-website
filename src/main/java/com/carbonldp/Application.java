package com.carbonldp;

import com.carbonldp.apps.context.AppContextConfig;
import com.carbonldp.config.ConfigurationConfig;
import com.carbonldp.config.RepositoriesConfig;
import com.carbonldp.config.ServicesConfig;
import com.carbonldp.log.LOGConfig;
import com.carbonldp.log.RequestLoggerFilter;
import com.carbonldp.mail.MailConfig;
import com.carbonldp.repository.RepositoriesUpdater;
import com.carbonldp.repository.security.SecuredNativeStoreFactory;
import com.carbonldp.repository.txn.TxnConfig;
import com.carbonldp.security.SecurityConfig;
import com.carbonldp.web.config.WebConfig;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.fusesource.jansi.AnsiConsole;
import org.openrdf.sail.config.SailRegistry;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.RequestContextFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Properties;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class Application {
	public static void main( String[] args ) throws Exception {
		new Application().start();
	}

	private void start() throws Exception {
		AnsiConsole.systemInstall();

		Vars.initialize();

		Properties errorCodes = loadErrorCodes();
		ErrorCodes.populate( errorCodes );

		SailRegistry.getInstance().add( new SecuredNativeStoreFactory() );

		RepositoriesUpdater repositoriesUpdater = new RepositoriesUpdater();
		if ( ! repositoriesUpdater.repositoriesAreUpToDate() ) repositoriesUpdater.updateRepositories();

		Server server = new Server( 8083 );

		WebAppContext contextHandler = new WebAppContext();
		contextHandler.setErrorHandler( null );
		contextHandler.setContextPath( "/" );
		contextHandler.setResourceBase( "." );
		contextHandler.setConfigurations( new Configuration[]{} );

		contextHandler.addEventListener( new ServletContextLoaderListener( createRootContext() ) {
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

		server.setHandler( contextHandler );
		server.start();
		server.join();
	}

	private Properties loadErrorCodes() throws ServletException {
		Resource propertiesFile = new ClassPathResource( "error-codes.properties" );
		PropertiesFactoryBean factory = new PropertiesFactoryBean();
		factory.setLocation( propertiesFile );
		Properties properties;

		try {
			factory.afterPropertiesSet();
			properties = factory.getObject();
		} catch ( IOException e ) {
			throw new ServletException( "Couldn't load the error codess file.", e );
		}

		return properties;
	}

	private AnnotationConfigWebApplicationContext createRootContext() {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(
			TxnConfig.class,
			ConfigurationConfig.class,
			RepositoriesConfig.class,
			AppContextConfig.class,
			SecurityConfig.class,
			ServicesConfig.class,
			MailConfig.class,
			LOGConfig.class
		);
		return rootContext;
	}

	private void addContextLifecycleManagerListener( WebApplicationContext context, ServletContext container ) {
		container.addListener( new ServletContextLoaderListener( context ) );
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

	private class ServletContextLoaderListener extends ContextLoaderListener implements ServletContextListener {
		public ServletContextLoaderListener( WebApplicationContext context ) {
			super( context );
		}
	}
}
