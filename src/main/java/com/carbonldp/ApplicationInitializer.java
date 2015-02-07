package com.carbonldp;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.Log4jConfigListener;

import com.carbonldp.apps.context.AppContextConfig;
import com.carbonldp.repository.RepositoryConfig;
import com.carbonldp.security.SecurityConfig;
import com.carbonldp.web.WebConfig;

public class ApplicationInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext container) throws ServletException {

		addLog4jListener(container);

		AnnotationConfigWebApplicationContext rootContext = createRootContext();
		addContextLifecycleManagerListener(rootContext, container);

		addSecurityFilterChain(container);

		AnnotationConfigWebApplicationContext dispatcherContext = createDispatcherContext();
		ServletRegistration.Dynamic dispatcher = registerDispatcherServlet(dispatcherContext, container);

		setMultipartConfig(dispatcher);
	}

	private void addLog4jListener(ServletContext container) {
		container.setInitParameter("log4jConfigLocation", "classpath:log4j.properties");
		container.addListener(new Log4jConfigListener());
	}

	private AnnotationConfigWebApplicationContext createRootContext() {
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		//@formatter:off
		rootContext.register(
			ConfigurationConfig.class,
			SecurityConfig.class,
			RepositoryConfig.class,
			AppContextConfig.class
		);
		//@formatter:on
		return rootContext;
	}

	private void addContextLifecycleManagerListener(WebApplicationContext context, ServletContext container) {
		container.addListener(new ContextLoaderListener(context));
	}

	private void addSecurityFilterChain(ServletContext container) {
		FilterRegistration.Dynamic securityFilter = container.addFilter(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME,
				new DelegatingFilterProxy(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME));
		securityFilter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
	}

	private AnnotationConfigWebApplicationContext createDispatcherContext() {
		AnnotationConfigWebApplicationContext dispatcherContext = new AnnotationConfigWebApplicationContext();
		//@formatter:off
		dispatcherContext.register(
			WebConfig.class	
		);
		//@formatter:on
		return dispatcherContext;
	}

	private ServletRegistration.Dynamic registerDispatcherServlet(AnnotationConfigWebApplicationContext dispatcherContext, ServletContext container) {
		ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", new DispatcherServlet(dispatcherContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
		return dispatcher;
	}

	private void setMultipartConfig(ServletRegistration.Dynamic dispatcher) {
		// TODO: When implementing multipart, verify these settings
		dispatcher.setMultipartConfig(new MultipartConfigElement("/tmp", 1024 * 1024 * 5, 1024 * 1024 * 5 * 5, 1024 * 1024));
	}

}
