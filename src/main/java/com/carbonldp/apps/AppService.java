package com.carbonldp.apps;

import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Set;

public interface AppService {
	public boolean exists( URI appURI );

	@PreAuthorize( "hasPermission(#appURI, 'READ')" )
	public App get( URI appURI );

	@PreAuthorize( "hasPermission(#appURI, 'READ')" )
	public Set<String> getDomains( URI appURI );

	public void create( App app );

	public void addDomain( String domain );

	public void setDomains( Set<String> domains );

	public void removeDomain( String domain );

	@PreAuthorize( "hasPermission(#app, 'UPDATE')" )
	public void replace( App app );

	@PreAuthorize( "hasPermission(#appURI, 'DELETE')" )
	public void delete( URI appURI );
}
