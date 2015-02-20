package com.carbonldp;

import static com.carbonldp.Consts.EMPTY_STRING;
import static com.carbonldp.Consts.SLASH;

import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;

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
	@Value("${platform.privileges.container}")
	private String platformPrivilegesContainer;
	@Value("${platform.privileges.container.url}")
	private String platformPrivilegesContainerURL;

	@Value("${apps.entrypoint}")
	private String appsEntryPoint;
	@Value("${apps.entrypoint.url}")
	private String appsEntryPointURL;

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

	@Override
	public String getPlatformPrivilegesContainer() {
		return platformPrivilegesContainer;
	}

	@Override
	public String getPlatformPrivilegesContainerURL() {
		return platformPrivilegesContainerURL;
	}

	public String getAppsEntryPoint() {
		return appsEntryPoint;
	}

	public String getAppsEntryPointURL() {
		return appsEntryPointURL;
	}

	public String getRealmName() {
		return realmName;
	}

	@Override
	public boolean isGenericRequest(String uri) {
		AntPathMatcher matcher = new AntPathMatcher();
		uri = uri.replace(getPlatformURL(), SLASH);

		return matcher.match(getGenericRequestPattern(), uri);
	}

	@Override
	public String getGenericRequestSlug(String uri) {
		AntPathMatcher matcher = new AntPathMatcher();
		uri = uri.replace(getPlatformURL(), EMPTY_STRING);

		// The matcher removes the ending slash (if it finds one)
		boolean hasTrailingSlash = uri.endsWith(SLASH);

		uri = matcher.extractPathWithinPattern(getGenericRequestPattern(), uri);

		int index = uri.indexOf(SLASH);
		if ( index == - 1 ) {
			// The timestamp is the last piece of the generic request URI
			return null;
		}
		if ( (index + 1) == uri.length() ) {
			// "/" is the last character
			return null;
		}

		StringBuilder slugBuilder = new StringBuilder();
		slugBuilder.append(uri.substring(index + 1));
		if ( hasTrailingSlash ) slugBuilder.append(SLASH);

		return slugBuilder.toString();
	}

	public String forgeGenericRequestURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append(this.genericRequestURL).append(Math.abs(random.nextLong()));
		if ( enforceEndingSlash() ) urlBuilder.append(SLASH);
		return urlBuilder.toString();
	}

	private String getGenericRequestPattern() {
		StringBuilder patternBuilder = new StringBuilder();
		if ( ! this.genericRequest.startsWith(SLASH) ) patternBuilder.append(SLASH);
		patternBuilder.append(this.genericRequest);
		if ( ! this.genericRequest.endsWith(SLASH) ) patternBuilder.append(SLASH);
		patternBuilder.append("?*/**/");
		return patternBuilder.toString();
	}

	public Boolean enforceEndingSlash() {
		return _enforceEndingSlash;
	}

}
