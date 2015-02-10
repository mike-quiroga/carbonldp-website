package com.carbonldp;

import static com.carbonldp.commons.Consts.SLASH;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;

public class PropertiesFileConfigurationRepository extends AbstractComponent implements ConfigurationRepository {

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
	@Value("${platform.roles.container}")
	private String platformRolesContainer;
	@Value("${platform.roles.container.url}")
	private String platformRolesContainerURL;

	@Value("${applications.entrypoint}")
	private String applicationsEntryPoint;
	@Value("${applications.entrypoint.url}")
	private String applicationsEntryPointURL;

	@Value("${generic-request}")
	private String genericRequest;
	@Value("${generic-request.url}")
	private String genericRequestURL;

	@Value("${authentication.realm-name}")
	private String realmName;

	@Value("${config.enforce-ending-slash}")
	private Boolean _enforceEndingSlash;

	private Random random;

	public PropertiesFileConfigurationRepository() {
		this.random = new Random();
	}

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

	@Override
	public String getPlatformRolesContainer() {
		return platformRolesContainer;
	}

	@Override
	public String getPlatformRolesContainerURL() {
		return platformRolesContainerURL;
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

	public String forgeGenericRequestURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(this.genericRequestURL).append(random.nextLong());
		if ( enforceEndingSlash() ) urlBuilder.append(SLASH);
		return urlBuilder.toString();
	}

	public Boolean enforceEndingSlash() {
		return _enforceEndingSlash;
	}

}
