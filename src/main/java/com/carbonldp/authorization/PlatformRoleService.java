package com.carbonldp.authorization;

import java.util.Set;

import com.carbonldp.agents.Agent;

public interface PlatformRoleService {
	public Set<PlatformRole> getPlatformRolesOfAgent(Agent agent);
}
