package com.carbonldp.apps;

import org.openrdf.model.IRI;

public interface AppRepository {
	public boolean exists( IRI appIRI );

	public App get( IRI appIRI );

	public App findByRootContainer( IRI rootContainerIRI );

	public App createPlatformAppRepository( App app );

	public void delete( IRI appIRI );

	public IRI getPlatformAppContainerIRI();
}
