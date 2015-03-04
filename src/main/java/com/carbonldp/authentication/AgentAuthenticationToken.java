package com.carbonldp.authentication;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.util.Assert;

import com.carbonldp.agents.Agent;
import com.carbonldp.apps.AppRole;
import com.carbonldp.authorization.Platform;

public class AgentAuthenticationToken extends AbstractAuthenticationToken implements Authentication, CredentialsContainer {

	private static final long serialVersionUID = - 8845911646804638633L;

	private final Agent agent;

	private final Map<URI, Set<AppRole>> appsRoles;

	public AgentAuthenticationToken(Agent agent, Set<Platform.Role> platformRoles, Set<Platform.Privilege> platformPrivileges, Map<URI, Set<AppRole>> appsRoles) {
		super(platformRoles, platformPrivileges);

		Assert.notNull(agent);
		this.agent = agent;

		Map<URI, Set<AppRole>> tempAppsRoles = new HashMap<URI, Set<AppRole>>();
		for (URI appURI : appsRoles.keySet()) {
			Set<AppRole> tempAppRoles = new HashSet<AppRole>();
			for (AppRole appRole : appsRoles.get(appURI)) {
				Assert.notNull(appRole);
				tempAppRoles.add(appRole);
			}
			tempAppsRoles.put(appURI, Collections.unmodifiableSet(tempAppRoles));
		}
		this.appsRoles = Collections.unmodifiableMap(tempAppsRoles);
	}

	@Override
	public String getName() {
		return "Agent: " + agent.getUsername();
	}

	public Map<URI, Set<AppRole>> getAppsRoles() {
		return this.appsRoles;
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
