package com.carbonldp.authentication;

import com.carbonldp.agents.Agent;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.roles.AppRolesHolder;
import com.carbonldp.authorization.Platform;
import org.openrdf.model.URI;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

public class AgentAuthenticationToken extends AbstractAuthenticationToken implements AppRolesHolder, Authentication, CredentialsContainer {

	private static final long serialVersionUID = - 8845911646804638633L;

	private final Agent agent;

	private final Map<URI, Set<AppRole>> appsRoles;

	public AgentAuthenticationToken( Agent agent, Set<Platform.Role> platformRoles, Set<Platform.Privilege> platformPrivileges ) {
		super( platformRoles, platformPrivileges );

		Assert.notNull( agent );
		this.agent = agent;
		this.appsRoles = new HashMap<>();
	}

	@Override
	public Map<URI, Set<AppRole>> getAppsRoles() {
		return Collections.unmodifiableMap( appsRoles );
	}

	@Override
	public Set<AppRole> getAppRoles( URI appURI ) {
		if ( ! appsRoles.containsKey( appURI ) ) return new HashSet<>();
		return appsRoles.get( appURI );
	}

	@Override
	public void setAppRoles( Map<URI, Set<AppRole>> appsRoles ) {
		for ( URI appURI : appsRoles.keySet() ) {
			Set<AppRole> appRoles = appsRoles.get( appURI );
			setAppRoles( appURI, appRoles );
		}
	}

	@Override
	public void setAppRoles( URI appURI, Collection<AppRole> appRoles ) {
		Set<AppRole> tempAppRoles = appRoles.stream()
											.filter( appRole -> appRole != null )
											.collect( Collectors.toSet() );
		appsRoles.put( appURI, Collections.unmodifiableSet( tempAppRoles ) );
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
