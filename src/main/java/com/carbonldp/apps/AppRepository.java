package com.carbonldp.apps;

import org.openrdf.model.IRI;

import java.util.Set;

public interface AppRepository {
	public boolean exists( IRI appIRI );

	public App get( IRI appIRI );

	public Set<App> get( Set<IRI> appIRIs );

	public App findByRootContainer( IRI rootContainerIRI );

	public App createPlatformAppRepository( App app );

	public void delete( IRI appIRI );

	public IRI getPlatformAppContainerIRI();
}
