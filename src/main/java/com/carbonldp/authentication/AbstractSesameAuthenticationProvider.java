package com.carbonldp.authentication;

import java.util.Set;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentService;
import com.carbonldp.authorization.PlatformPrivilege;
import com.carbonldp.authorization.PlatformPrivilegeService;
import com.carbonldp.authorization.PlatformRole;
import com.carbonldp.authorization.PlatformRoleService;

public abstract class AbstractSesameAuthenticationProvider extends AbstractAuthenticationProvider {
	protected final AgentService agentService;
	protected final PlatformRoleService platformRoleService;
	protected final PlatformPrivilegeService platformPrivilegeService;

	public AbstractSesameAuthenticationProvider(AgentService agentService, PlatformRoleService platformRoleService,
			PlatformPrivilegeService platformPrivilegeService) {
		this.agentService = agentService;
		this.platformRoleService = platformRoleService;
		this.platformPrivilegeService = platformPrivilegeService;
	}

	protected AgentAuthenticationToken createAgentAuthenticationToken(Agent agent) {
		Set<PlatformRole> platformRoles = platformRoleService.getPlatformRolesOfAgent(agent);
		Set<PlatformPrivilege> platformPrivileges = platformPrivilegeService.get(platformRoles);

		AgentAuthenticationToken token = new AgentAuthenticationToken(agent, platformRoles, platformPrivileges);
		token.eraseCredentials();
		token.setAuthenticated(true);
		return token;
	}

}
