package com.base22.carbon.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.base22.carbon.apps.Application;

// TODO: This class needs to contain the application the master key points to
public class MasterKeyAuthenticationToken extends AbstractAuthenticationToken implements ApplicationContextToken {

	private static final long serialVersionUID = 3084067928540042807L;

	private String masterKey = null;
	private Application currentApplicationContext = null;

	public MasterKeyAuthenticationToken(String masterKey) {
		super(null);
		this.masterKey = masterKey;
		setAuthenticated(false);
	}

	public MasterKeyAuthenticationToken(Application application, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		// TODO: Decide how the application context will be managed
	}

	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		if ( isAuthenticated ) {
			throw new IllegalArgumentException("Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
		}

		super.setAuthenticated(false);
	}

	public String getMasterKey() {
		return (String) getCredentials();
	}

	@Override
	public Object getCredentials() {
		return masterKey;
	}

	@Override
	public Object getPrincipal() {
		return null;
	}

	@Override
	public void eraseCredentials() {
		super.eraseCredentials();
		masterKey = null;
	}

	public Application getCurrentApplicationContext() {
		return currentApplicationContext;
	}

	public void setCurrentApplicationContext(Application currentApplicationContext) {
		this.currentApplicationContext = currentApplicationContext;
	}

}
