package com.base22.carbon.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.base22.carbon.repository.RepositoryServiceException;
import com.base22.carbon.repository.services.RepositoryService;
import com.github.jsonldjava.jena.JenaJSONLD;

// TODO: Find a way to inject dependencies to this class
public class BootstrapServletContextListener implements ServletContextListener {

	static final Logger LOG = LoggerFactory.getLogger(BootstrapServletContextListener.class);

	@Autowired
	protected RepositoryService repositoryService;

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
