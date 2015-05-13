package com.carbonldp.authorization;

import com.carbonldp.agents.Agent;
import org.openrdf.model.URI;

import java.util.Set;

public interface PlatformRoleRepository {
	public Set<PlatformRole> get( Set<URI> platformRoleURIs );

	public Set<PlatformRole> get( Agent agent );

	public Set<Platform.Role> getRepresentations( Set<PlatformRole> platformRoleResources );
}
