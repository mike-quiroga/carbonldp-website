package com.base22.carbon.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.base22.carbon.repository.RepositoryServiceException;
import com.base22.carbon.repository.services.RepositoryService;
import com.github.jsonldjava.jena.JenaJSONLD;

@WebListener
public class BootstrapServletContextListener implements ApplicationContextAware, ServletContextListener {

	static final Logger LOG = LoggerFactory.getLogger(BootstrapServletContextListener.class);

	@Autowired
	protected RepositoryService repositoryService;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if ( applicationContext instanceof WebApplicationContext ) {
			((WebApplicationContext) applicationContext).getServletContext().addListener(this);
		} else {
			// Either throw an exception or fail gracefully, up to you
			throw new RuntimeException("Must be inside a web application context");
		}
	}

	public void contextDestroyed(ServletContextEvent evt) {
		if ( repositoryService != null ) {
			releaseRepositoryService();
		}
	}

	public void contextInitialized(ServletContextEvent sce) {
		//@formatter:off
		WebApplicationContextUtils
	        .getRequiredWebApplicationContext(sce.getServletContext())
	        .getAutowireCapableBeanFactory()
	        .autowireBean(this);
		//@formatter:on

		// Initializing JSON-LD Jena support
		initializeJSONLDSupport();
	}

	private void initializeJSONLDSupport() {
		JenaJSONLD.init();
	}

	private void releaseRepositoryService() {
		try {
			repositoryService.release();
		} catch (RepositoryServiceException ignore) {
		}
	}

}
