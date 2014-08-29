package com.base22.carbon.services;

import java.util.Set;

import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.base22.carbon.constants.Carbon;
import com.base22.carbon.security.models.Application;
import com.github.jsonldjava.jena.JenaJSONLD;

@Service
public class ConfigurationService {
	static final Logger LOG = LoggerFactory.getLogger(ConfigurationService.class);

	private final String uploadsPath = "/opt/carbon/uploads/";

	private final String APPLICATION_ROLE_DEFAULT_NAME = "Application Administrators";

	private final String PLATFORM_DATASET = "platform";
	private final String PLATFORM_TEST_DATASET = "platform-test";

	private final int TOKEN_COOKIE_LIFE = 5;

	private final String DEFAULT_PLATFORM_ROLE = "ROLE_APP_DEVELOPER";

	public void init() {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> init()");
		}

		// Initializing JSON-LD Jena support
		initializeJSONLDSupport();
	}

	private void initializeJSONLDSupport() {
		JenaJSONLD.init();
	}

	/**
	 * Returns the language (content type) that is configured as the default for the server (i.e. text/turtle,
	 * application/ld+json), application/xml).
	 * 
	 * Note: In order to maintain compliance with W3C LDP 1.0, LDP servers must provide a text/turtle representation of
	 * the requested LDP-RS whenever HTTP content negotiation does not force another outcome. In other words, if the
	 * server receives a GET request whose Request-URI identifies a LDP-RS, and either text/turtle has the highest
	 * relative quality factor (q=value) in the Accept request header or that header is absent, then an LDP server has
	 * to respond with Turtle.
	 * 
	 * We might consider making this a configurable attribute in the near future, but for now, the default needs to be
	 * TURTLE in order to pass ldp-testsuite.
	 * 
	 * @return
	 */
	public Lang getDefaultLanguage() {
		// return Lang.JSONLD;
		return Lang.TURTLE;
	}

	public String getUploadsPath() {
		String uploadsPath = null;

		uploadsPath = this.uploadsPath;

		return uploadsPath;
	}

	public String getApplicationUploadsPath(Application application) {
		StringBuilder uploadsPathBuilder = new StringBuilder();
		uploadsPathBuilder.append(this.uploadsPath).append(application.getUuidString());
		return uploadsPathBuilder.toString();
	}

	public String getDefaultRootApplicationRoleName() {
		return APPLICATION_ROLE_DEFAULT_NAME;
	}

	public String getPlatformDatasetName() {
		return PLATFORM_DATASET;
	}

	public Set<String> getReservedApplicationNames() {
		return Carbon.RESERVED_APPLICATION_NAMES;
	}

	public String getServerDomain() {
		return Carbon.DOMAIN;
	}

	public String getServerURL() {
		return Carbon.URL;
	}

	public int getTokenCookieLifeInMinutes() {
		return TOKEN_COOKIE_LIFE;
	}

	public boolean createAPIKeyForNewAgents() {
		return true;
	}

	// TODO: Move this?...
	public String getDefaultPlatformRole() {
		return DEFAULT_PLATFORM_ROLE;
	}
}
