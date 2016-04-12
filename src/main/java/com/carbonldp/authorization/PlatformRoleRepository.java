package com.carbonldp.authorization;

import com.carbonldp.agents.Agent;
import org.openrdf.model.IRI;

import java.util.Set;

public interface PlatformRoleRepository {
	public Set<PlatformRole> get( Set<IRI> platformRoleIRIs );

	public Set<PlatformRole> get( Agent agent );

	public Set<Platform.Role> getRepresentations( Set<PlatformRole> platformRoleResources );
}
