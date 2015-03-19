package com.carbonldp.authentication;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.authorization.*;

import java.util.Set;

public abstract class AbstractSesameAuthenticationProvider extends AbstractAuthenticationProvider {
	protected final AgentRepository agentRepository;

	protected final PlatformRoleRepository platformRoleRepository;
	protected final PlatformPrivilegeRepository platformPrivilegeRepository;

	public AbstractSesameAuthenticationProvider( AgentRepository agentRepository, PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		this.agentRepository = agentRepository;
		this.platformRoleRepository = platformRoleRepository;
		this.platformPrivilegeRepository = platformPrivilegeRepository;
	}

	protected AgentAuthenticationToken createAgentAuthenticationToken( Agent agent ) {
		Set<PlatformRole> platformRoles = platformRoleRepository.get( agent );
		Set<PlatformPrivilege> platformPrivileges = platformPrivilegeRepository.get( platformRoles );

		Set<Platform.Role> platformRoleRepresentations = platformRoleRepository.getRepresentations( platformRoles );
		Set<Platform.Privilege> platformPrivilegeRepresentations = platformPrivilegeRepository.getRepresentations( platformPrivileges );

		AgentAuthenticationToken token = new AgentAuthenticationToken( agent, platformRoleRepresentations, platformPrivilegeRepresentations );
		token.eraseCredentials();
		token.setAuthenticated( true );
		return token;
	}
}
