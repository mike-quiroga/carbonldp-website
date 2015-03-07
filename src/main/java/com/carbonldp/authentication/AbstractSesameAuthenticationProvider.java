package com.carbonldp.authentication;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentService;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.authorization.*;
import com.carbonldp.spring.Inject;
import org.openrdf.model.URI;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class AbstractSesameAuthenticationProvider extends AbstractAuthenticationProvider {
	protected AgentService agentService;

	protected PlatformRoleService platformRoleService;
	protected PlatformPrivilegeService platformPrivilegeService;

	protected AgentAuthenticationToken createAgentAuthenticationToken(Agent agent) {
		Set<PlatformRole> platformRoles = platformRoleService.get( agent );
		Set<PlatformPrivilege> platformPrivileges = platformPrivilegeService.get( platformRoles );

		Set<Platform.Role> platformRoleRepresentations = platformRoleService.getRepresentations( platformRoles );
		Set<Platform.Privilege> platformPrivilegeRepresentations = platformPrivilegeService.getRepresentations( platformPrivileges );

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
	public void setAgentService(AgentService agentService) {
		this.agentService = agentService;
	}

	@Inject
	public void setPlatformRoleService(PlatformRoleService platformRoleService) {
		this.platformRoleService = platformRoleService;
	}

	@Inject
	public void setPlatformPrivilegeService(PlatformPrivilegeService platformPrivilegeService) {
		this.platformPrivilegeService = platformPrivilegeService;
	}
}
