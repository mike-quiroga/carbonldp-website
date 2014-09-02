package com.base22.carbon.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.base22.carbon.agents.Agent;
import com.base22.carbon.apps.Application;

public class AgentAuthenticationToken extends AbstractAuthenticationToken implements ApplicationContextToken {

	private static final long serialVersionUID = - 3339698074388178968L;

	private Agent principal = null;
	private Application currentApplicationContext = null;

	public AgentAuthenticationToken(Agent principal, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		setAuthenticated(true);
	}

	public Agent getPrincipal() {
		return this.principal;
	}

	public Object getCredentials() {
		return null;
	}

	@Override
	public void eraseCredentials() {
		principal.eraseCredentials();
	}

	public Application getCurrentApplicationContext() {
		return currentApplicationContext;
	}

	public void setCurrentApplicationContext(Application currentApplicationContext) {
		this.currentApplicationContext = currentApplicationContext;
	}

}
