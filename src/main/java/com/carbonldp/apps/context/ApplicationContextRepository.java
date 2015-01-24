package com.carbonldp.apps.context;

import org.openrdf.model.URI;

import com.carbonldp.apps.Application;
import com.carbonldp.apps.ApplicationService;

/**
 * Retrieves an <code>Application</code> to save it in the <code>ApplicationContext</code>. Can implement it's own cache
 * system to retrieve Applications more efficiently.
 * 
 * @author MiguelAraCo
 *
 */
public class ApplicationContextRepository {
	private ApplicationService applicationService;

	public ApplicationContextRepository(ApplicationService applicationService) {
		this.applicationService = applicationService;
	}

	public Application getApplication(URI applicationURI) {
		// TODO: Implement
		return null;
	}
}
