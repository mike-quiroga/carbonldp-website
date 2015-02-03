package com.carbonldp;

public interface ConfigurationRepository {
	public String getAppsRepositoryDirectory();

	public String getPlatformRepositoryDirectory();

	public String getPlatformURL();

	public String getPlatformContainer();

	public String getPlatformContainerURL();

	public String getPlatformAppsContainer();

	public String getPlatformAppsContainerURL();

	public String getPlatformAgentsContainer();

	public String getPlatformAgentsContainerURL();

	public String getApplicationsEntryPoint();

	public String getApplicationsEntryPointURL();

	public String getRealmName();

	public Boolean enforceEndingSlash();
}
