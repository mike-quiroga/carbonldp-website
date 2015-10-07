package com.carbonldp.config;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Vars;
import com.carbonldp.mail.MailSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.AntPathMatcher;

import java.util.Random;

import static com.carbonldp.Consts.EMPTY_STRING;
import static com.carbonldp.Consts.SLASH;

public class PropertiesFileConfigurationRepository extends AbstractComponent implements ConfigurationRepository {

	@Value( "${authentication.realm-name}" )
	private String realmName;

	@Value( "${config.enforce-ending-slash}" )
	private Boolean enforceEndingSlash;

	@Value( "${config.platform.agents.require-validation}" )
	private boolean requireAgentValidation;

	@Value( "${config.mail.host}" )
	private String mailHost;
	@Value( "${config.mail.protocol}" )
	private String mailProtocol;
	@Value( "${config.mail.port}" )
	private int mailPort;
	@Value( "${config.mail.username}" )
	private String mailUsername;
	@Value( "${config.mail.password}" )
	private String mailPassword;
	@Value( "${config.mail.smtp.auth:false}" )
	private boolean useSMTPAuth;
	@Value( "${config.mail.smtp.tls:false}" )
	private boolean useTLS;

	private final Random random;

	public PropertiesFileConfigurationRepository() {
		this.random = new Random();
	}

	public String getRealmName() {
		return realmName;
	}

	@Override
	public boolean isGenericRequest( String uri ) {
		AntPathMatcher matcher = new AntPathMatcher();
		uri = uri.replace( Vars.getInstance().getHost(), SLASH );

		return matcher.match( getGenericRequestPattern(), uri );
	}

	@Override
	public String getGenericRequestSlug( String uri ) {
		AntPathMatcher matcher = new AntPathMatcher();
		uri = uri.replace( Vars.getInstance().getHost(), EMPTY_STRING );

		// The matcher removes the ending slash (if it finds one)
		boolean hasTrailingSlash = uri.endsWith( SLASH );

		uri = matcher.extractPathWithinPattern( getGenericRequestPattern(), uri );

		int index = uri.indexOf( SLASH );
		if ( index == - 1 ) {
			// The timestamp is the last piece of the generic request URI
			return null;
		}
		if ( ( index + 1 ) == uri.length() ) {
			// "/" is the last character
			return null;
		}

		StringBuilder slugBuilder = new StringBuilder();
		slugBuilder.append( uri.substring( index + 1 ) );
		if ( hasTrailingSlash ) slugBuilder.append( SLASH );

		return slugBuilder.toString();
	}

	public String forgeGenericRequestURL() {
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append( Vars.getInstance().getGenericRequestURL() ).append( Math.abs( random.nextLong() ) );
		if ( enforceEndingSlash() ) urlBuilder.append( SLASH );
		return urlBuilder.toString();
	}

	private String getGenericRequestPattern() {
		StringBuilder patternBuilder = new StringBuilder();
		if ( ! Vars.getInstance().getGenericRequest().startsWith( SLASH ) ) patternBuilder.append( SLASH );
		patternBuilder.append( Vars.getInstance().getGenericRequest() );
		if ( ! Vars.getInstance().getGenericRequest().endsWith( SLASH ) ) patternBuilder.append( SLASH );
		patternBuilder.append( "?*/**/" );
		return patternBuilder.toString();
	}

	public Boolean enforceEndingSlash() {
		return enforceEndingSlash;
	}

	@Override
	public boolean requireAgentEmailValidation() {
		return requireAgentValidation;
	}

	@Override
	public MailSettings getMailSettings() {
		MailSettings settings = new MailSettings();

		settings.setHost( mailHost );
		settings.setPort( mailPort );
		settings.setProtocol( mailProtocol );
		settings.setUsername( mailUsername );
		settings.setPassword( mailPassword );
		settings.useSMTPAuth( useSMTPAuth );
		settings.useTLS( useTLS );

		return settings;
	}
}
