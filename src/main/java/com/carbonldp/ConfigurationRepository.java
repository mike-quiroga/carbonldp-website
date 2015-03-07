package com.carbonldp;

public interface ConfigurationRepository {
	public String getRealmName();

	public boolean isGenericRequest( String uri );

	public String getGenericRequestSlug( String uri );

	public String forgeGenericRequestURL();

	public Boolean enforceEndingSlash();
}
