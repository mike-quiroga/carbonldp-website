package com.carbonldp.apps;

import org.openrdf.model.URI;

import com.carbonldp.apps.App;

public interface AppService {
	public App get(URI applicationURI);
}
