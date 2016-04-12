package com.carbonldp.authentication;

import com.carbonldp.agents.Agent;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.roles.AppRolesHolder;
import com.carbonldp.authorization.Platform;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class AgentAuthenticationToken extends AbstractAuthenticationToken implements AppRolesHolder, Authentication, CredentialsContainer {

	private static final long serialVersionUID = - 8845911646804638633L;

	private final Agent agent;

	private final Set<AppRole> appRoles;

	public AgentAuthenticationToken( Agent agent, Set<Platform.Role> platformRoles, Set<Platform.Privilege> platformPrivileges ) {
		super( platformRoles, platformPrivileges );

		Assert.notNull( agent );
		this.agent = agent;
		this.appRoles = new HashSet<>();
	}

	@Override
	public Set<AppRole> getAppRoles() {
		return Collections.unmodifiableSet( appRoles );
	}

	@Override
	public void setAppRoles( Set<AppRole> appRoles ) {
		this.appRoles.clear();
		this.appRoles.addAll( appRoles );
	}

	@Override
	public String getName() {
		return "Agent: " + agent.getUsername();
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	public Agent getAgent() {
		return agent;
	}

	@Override
	public Object getPrincipal() {
		return agent;
	}

	@Override
	public void eraseCredentials() {
		agent.eraseCredentials();
	}
}
