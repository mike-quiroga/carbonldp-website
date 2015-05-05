package com.carbonldp.mail;

import com.carbonldp.Consts;

import java.util.Properties;

public class MailSettings {

	private static final String SMTP_AUTH_PROPERTY = "mail.smtp.auth";
	private static final String SMTP_TLS_PROPERTY = "mail.smtp.starttls.enable";

	private String host;
	private String protocol;
	private int port;
	private String username;
	private String password;

	private boolean smtpAuth = false;
	private boolean tls = false;

	public String getHost() {
		return host;
	}

	public void setHost( String host ) {
		this.host = host;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol( String protocol ) {
		this.protocol = protocol;
	}

	public int getPort() {
		return port;
	}

	public void setPort( int port ) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername( String username ) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}

	public boolean useSMTPAuth() {
		return smtpAuth;
	}

	public void useSMTPAuth( boolean smtpAuth ) {
		this.smtpAuth = smtpAuth;
	}

	public boolean useTLS() {
		return tls;
	}

	public void useTLS( boolean tls ) {
		this.tls = tls;
	}

	public Properties getJavaMailProperties() {
		Properties mailProperties = new Properties();
		if ( useSMTPAuth() ) mailProperties.setProperty( SMTP_AUTH_PROPERTY, Consts.TRUE );
		if ( useTLS() ) mailProperties.setProperty( SMTP_TLS_PROPERTY, Consts.TRUE );

		return mailProperties;
	}

}
