package com.carbonldp.authorization;

import java.util.Set;

import com.carbonldp.agents.Agent;

public interface PlatformRoleService {
	public Set<PlatformRole> get(Agent agent);

	public Set<Platform.Role> getRepresentations(Set<PlatformRole> platformRoleResources);
}
