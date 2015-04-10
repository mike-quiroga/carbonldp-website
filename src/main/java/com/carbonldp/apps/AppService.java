package com.carbonldp.apps;

import org.openrdf.model.URI;

import java.util.Set;

public interface AppService {
	public boolean exists( URI appURI );

	public App get( URI appURI );

	public Set<String> getDomains( URI appURI );

	public void create( App app );

	public void addDomain( String domain );

	public void setDomains( Set<String> domains );

	public void removeDomain( String domain );

	public void replace( App app );

	public void delete( App app );
}
