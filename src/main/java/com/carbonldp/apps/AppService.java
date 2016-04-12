package com.carbonldp.apps;

import org.openrdf.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AppService {
	public boolean exists( IRI appIRI );

	@PreAuthorize( "hasPermission(#appIRI, 'READ')" )
	public App get( IRI appIRI );

	public void create( App app );

	@PreAuthorize( "hasPermission(#app, 'UPDATE')" )
	public void replace( App app );

	@PreAuthorize( "hasPermission(#appIRI, 'DELETE')" )
	public void delete( IRI appIRI );
}
