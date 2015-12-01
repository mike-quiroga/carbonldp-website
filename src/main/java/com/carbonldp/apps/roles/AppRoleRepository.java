package com.carbonldp.apps.roles;

import com.carbonldp.agents.Agent;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRole;
import com.carbonldp.ldp.containers.Container;
import org.openrdf.model.URI;

import java.util.Set;

public interface AppRoleRepository {

	public AppRole get( URI appRoleURI );

	public Set<AppRole> get( Agent agent );

	public Set<AppRole> get( App app, Agent agent );

	public void addAgent( URI appRole, Agent agent );

	public AppRole create( AppRole appRole );

	public Container createAppRolesContainer( URI rootContainerURI );

	public boolean exists( URI appRoleURI );

	public Set<URI> getParentsURI( URI appRole );
}
