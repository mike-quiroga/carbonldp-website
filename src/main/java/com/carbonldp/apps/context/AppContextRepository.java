package com.carbonldp.apps.context;

import org.openrdf.model.URI;

import com.carbonldp.apps.AppService;
import com.carbonldp.apps.Application;

/**
 * Retrieves an <code>Application</code> to save it in the <code>ApplicationContext</code>. Can implement it's own cache
 * system to retrieve Applications more efficiently.
 * 
 * @author MiguelAraCo
 *
 */
public class AppContextRepository {
	private AppService appService;

	public AppContextRepository(AppService appService) {
		this.appService = appService;
	}

	public Application getApplication(URI applicationURI) {
		// TODO: Implement
		return null;
	}
}
