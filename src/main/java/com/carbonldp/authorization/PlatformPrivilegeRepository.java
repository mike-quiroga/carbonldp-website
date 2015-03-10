package com.carbonldp.authorization;

import java.util.Set;

public interface PlatformPrivilegeRepository {
	public Set<PlatformPrivilege> get( Set<PlatformRole> platformRoles );

	public Set<Platform.Privilege> getRepresentations( Set<PlatformPrivilege> platformPrivilegeResources );
}
