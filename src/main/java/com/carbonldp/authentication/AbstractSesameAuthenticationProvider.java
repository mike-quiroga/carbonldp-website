package com.carbonldp.authentication;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authorization.*;
import com.carbonldp.spring.Inject;
import org.openrdf.model.URI;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractSesameAuthenticationProvider extends AbstractAuthenticationProvider {
	protected AgentRepository agentRepository;

	protected PlatformRoleRepository platformRoleRepository;
	protected PlatformPrivilegeRepository platformPrivilegeRepository;

	protected AgentAuthenticationToken createAgentAuthenticationToken(Agent agent) {
		Set<PlatformRole> platformRoles = platformRoleRepository.get( agent );
		Set<PlatformPrivilege> platformPrivileges = platformPrivilegeRepository.get( platformRoles );

		Set<Platform.Role> platformRoleRepresentations = platformRoleRepository.getRepresentations( platformRoles );
		Set<Platform.Privilege> platformPrivilegeRepresentations = platformPrivilegeRepository.getRepresentations( platformPrivileges );

		Map<URI, Set<AppRole>> appsRoles = getAppsRoles( agent );

		AgentAuthenticationToken token = new AgentAuthenticationToken( agent, platformRoleRepresentations, platformPrivilegeRepresentations, appsRoles );
		token.eraseCredentials();
		token.setAuthenticated( true );
		return token;
	}

	private Map<URI, Set<AppRole>> getAppsRoles(Agent agent) {
		if ( AppContextHolder.getContext().isEmpty() ) return new HashMap<URI, Set<AppRole>>();

		URI currentAppURI = AppContextHolder.getContext().getApplication().getURI();
		// TODO: Retrieve agent's current app approles
		return new HashMap<URI, Set<AppRole>>();
	}

	@Inject
	public void setAgentRepository(AgentRepository agentRepository ) {
		this.agentRepository = agentRepository;
	}

	@Inject
	public void setPlatformRoleRepository(PlatformRoleRepository platformRoleRepository ) {
		this.platformRoleRepository = platformRoleRepository;
	}

	@Inject
	public void setPlatformPrivilegeRepository(PlatformPrivilegeRepository platformPrivilegeRepository ) {
		this.platformPrivilegeRepository = platformPrivilegeRepository;
	}
}
