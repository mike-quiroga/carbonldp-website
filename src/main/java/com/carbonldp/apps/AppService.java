package com.carbonldp.apps;

import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AppService {
	public boolean exists( URI appURI );

	@PreAuthorize( "hasPermission(#appURI, 'READ')" )
	public App get( URI appURI );

	public void create( App app );

	@PreAuthorize( "hasPermission(#app, 'UPDATE')" )
	public void replace( App app );

	@PreAuthorize( "hasPermission(#appURI, 'DELETE')" )
	public void delete( URI appURI );
}
