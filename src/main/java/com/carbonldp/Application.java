package com.carbonldp;

import java.util.Properties;

/**
 * @author MiguelAraCo
 * @since 0.36.0
 */
public final class Application {
	private static Application instance;

	public static Application getInstance() {
		if ( instance == null ) Application.instance = new Application();
		return instance;
	}

	private Properties configuration;

	public Properties getConfiguration() {
		return configuration;
	}

	public void setConfiguration( Properties configuration ) {
		this.configuration = configuration;
	}
}
