package com.carbonldp.authorization;

import com.carbonldp.agents.Agent;

import java.util.Set;

public interface PlatformRoleService {
	public Set<PlatformRole> get(Agent agent);

	public Set<Platform.Role> getRepresentations(Set<PlatformRole> platformRoleResources);
}
