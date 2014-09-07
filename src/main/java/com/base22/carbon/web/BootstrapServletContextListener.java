package com.base22.carbon.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jsonldjava.jena.JenaJSONLD;

/**
 * Application Lifecycle Listener implementation class BootstrapServletContextListener
 * 
 */
public class BootstrapServletContextListener implements ServletContextListener {

	static final Logger LOG = LoggerFactory.getLogger(BootstrapServletContextListener.class);

	/**
	 * Default constructor.
	 */
	public BootstrapServletContextListener() {
		// TODO Auto-generated constructor stub
	}

	public void contextDestroyed(ServletContextEvent evt) {
		// TODO Auto-generated method stub

	}

	public void contextInitialized(ServletContextEvent evt) {
		// TODO Auto-generated method stub
		// Initializing JSON-LD Jena support
		initializeJSONLDSupport();
	}

	private void initializeJSONLDSupport() {
		JenaJSONLD.init();
	}

}
