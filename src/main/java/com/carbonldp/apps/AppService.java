package com.carbonldp.apps;

import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AppService {
	@PreAuthorize("hasPermission(#applicationURI, 'READ')")
	public Application get(URI applicationURI);
}
