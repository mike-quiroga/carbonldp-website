package com.carbonldp;

import org.springframework.beans.factory.annotation.Value;

public class PropertiesFileConfigurationRepository extends AbstractService implements ConfigurationRepository {

	@Value("${repositories.apps.directory}")
	private String appsRepositoryDirectory;
	@Value("${repositories.platform.directory}")
	private String platformRepositoryDirectory;

	@Value("${platform.url}")
	private String platformURL;
	@Value("${platform.container}")
	private String platformContainer;
	@Value("${platform.container.url}")
	private String platformContainerURL;
	@Value("${platform.apps.container}")
	private String platformAppsContainer;
	@Value("${platform.apps.container.url}")
	private String platformAppsContainerURL;
	@Value("${platform.agents.container}")
	private String platformAgentsContainer;
	@Value("${platform.agents.container.url}")
	private String platformAgentsContainerURL;
	@Value("${applications.entrypoint}")
	private String applicationsEntryPoint;
	@Value("${applications.entrypoint.url}")
	private String applicationsEntryPointURL;

	@Value("${authentication.realm-name}")
	private String realmName;

	@Value("${config.enforce-ending-slash}")
	private Boolean _enforceEndingSlash;

	public String getPlatformRepositoryDirectory() {
		return platformRepositoryDirectory;
	}

	public String getAppsRepositoryDirectory() {
		return appsRepositoryDirectory;
	}

	public String getPlatformURL() {
		return platformURL;
	}

	public String getPlatformContainer() {
		return platformContainer;
	}

	public String getPlatformContainerURL() {
		return platformContainerURL;
	}

	public String getPlatformAppsContainer() {
		return platformAppsContainer;
	}

	public String getPlatformAppsContainerURL() {
		return platformAppsContainerURL;
	}

	public String getPlatformAgentsContainer() {
		return platformAgentsContainer;
	}

	public String getPlatformAgentsContainerURL() {
		return platformAgentsContainerURL;
	}

	public String getApplicationsEntryPoint() {
		return applicationsEntryPoint;
	}

	public String getApplicationsEntryPointURL() {
		return applicationsEntryPointURL;
	}

	public String getRealmName() {
		return realmName;
	}

	public Boolean enforceEndingSlash() {
		return _enforceEndingSlash;
	}

}
