package com.carbonldp.authorization;

import java.util.Set;

public interface PlatformPrivilegeService {
	public Set<PlatformPrivilege> get(Set<PlatformRole> platformRoles);

	public Set<Platform.Privilege> getRepresentations(Set<PlatformPrivilege> platformPrivilegeResources);
}
