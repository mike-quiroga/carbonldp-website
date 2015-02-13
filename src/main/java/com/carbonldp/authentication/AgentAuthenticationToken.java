package com.carbonldp.authentication;

import static com.carbonldp.Consts.COMMA;
import static com.carbonldp.Consts.SPACE;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import com.carbonldp.agents.Agent;
import com.carbonldp.authorization.PlatformPrivilege;
import com.carbonldp.authorization.PlatformRole;

public class AgentAuthenticationToken implements Authentication, CredentialsContainer {

	private static final long serialVersionUID = - 8845911646804638633L;

	private final Agent agent;
	private final Set<PlatformRole> platformRoles;
	private final Set<PlatformPrivilege> platformPrivileges;
	private boolean authenticated = false;

	public AgentAuthenticationToken(Agent agent, Collection<? extends PlatformRole> platformRoles, Collection<? extends PlatformPrivilege> platformPrivileges) {
		Assert.notNull(agent);
		this.agent = agent;

		Set<PlatformRole> tempRoles = new HashSet<PlatformRole>();
		for (PlatformRole platformRole : platformRoles) {
			if ( platformRole == null ) throw new IllegalArgumentException("PlatformRoles collection cannot contain any null elements");
			tempRoles.add(platformRole);
		}
		this.platformRoles = Collections.unmodifiableSet(tempRoles);

		Set<PlatformPrivilege> tempPrivileges = new HashSet<PlatformPrivilege>();
		for (PlatformPrivilege platformPrivilege : platformPrivileges) {
			if ( platformPrivilege == null ) throw new IllegalArgumentException("PlatformPrivileges collection cannot contain any null elements");
			tempPrivileges.add(platformPrivilege);
		}
		this.platformPrivileges = Collections.unmodifiableSet(tempPrivileges);
	}

	@Override
	public String getName() {
		return agent.getUsername();
	}

	public Collection<PlatformRole> getPlatformRoles() {
		return this.platformRoles;
	}

	public Collection<PlatformPrivilege> getPlatformPrivileges() {
		return this.platformPrivileges;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return platformPrivileges;
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
	public boolean isAuthenticated() {
		return this.authenticated;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) {
		this.authenticated = isAuthenticated;
	}

	@Override
	public void eraseCredentials() {
		agent.eraseCredentials();
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();

		//@formatter:off
        stringBuilder
        	.append(super.toString()).append(": (")
        		.append("Agent: ").append(this.agent).append(COMMA).append(SPACE)
        		.append("Authenticated: ").append(this.isAuthenticated()).append(COMMA).append(SPACE)
        ;
        //@formatter:on

		//@formatter:off
        stringBuilder
        		.append("PlatformRoles: (")
        ;
        //@formatter:on
		Iterator<PlatformRole> platformRoleIterator = this.platformRoles.iterator();
		if ( ! platformRoleIterator.hasNext() ) stringBuilder.append("- NONE -");
		while (platformRoleIterator.hasNext()) {
			stringBuilder.append(platformRoleIterator.next()).append(COMMA).append(SPACE);
		}
		//@formatter:off
        stringBuilder
        		.append(")").append(COMMA).append(SPACE)
        ;
        //@formatter:on

		//@formatter:off
        stringBuilder
        		.append("PlatformPrivileges: (")
        ;
        //@formatter:on
		Iterator<PlatformPrivilege> platformPrivilegeIterator = this.platformPrivileges.iterator();
		if ( ! platformPrivilegeIterator.hasNext() ) stringBuilder.append("- NONE -");
		while (platformPrivilegeIterator.hasNext()) {
			stringBuilder.append(platformPrivilegeIterator.next()).append(COMMA).append(SPACE);
		}
		//@formatter:off
        stringBuilder
        		.append("))")
        ;
        //@formatter:on

		return stringBuilder.toString();
	}
}
