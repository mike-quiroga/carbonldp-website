package com.carbonldp.apps;

import org.openrdf.model.URI;

import com.carbonldp.commons.apps.App;

public interface AppService {
	public App get(URI applicationURI);
}
