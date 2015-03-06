package com.carbonldp.apps;

import org.openrdf.model.URI;

public interface AppService {
	public boolean exists( URI appURI );

	public App get( URI appURI );

	public App findByRootContainer( URI rootContainerURI );

	public App create( App app );

	public void initialize( App app );

}
