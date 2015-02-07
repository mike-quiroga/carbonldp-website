package com.carbonldp.apps;

import org.openrdf.model.URI;

public interface AppRepository {
	public Application get(URI applicationURI);
}
