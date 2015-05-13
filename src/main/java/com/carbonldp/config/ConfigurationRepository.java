package com.carbonldp.config;

import com.carbonldp.mail.MailSettings;

public interface ConfigurationRepository {
	public String getRealmName();

	public boolean isGenericRequest( String uri );

	public String getGenericRequestSlug( String uri );

	public String forgeGenericRequestURL();

	public Boolean enforceEndingSlash();

	public boolean requireAgentEmailValidation();

	public MailSettings getMailSettings();
}
