package com.carbonldp.apps.roles;

import com.carbonldp.agents.Agent;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRole;
import com.carbonldp.ldp.containers.Container;
import org.openrdf.model.IRI;

import java.util.Set;

public interface AppRoleRepository {
	public boolean exists( IRI appRoleIRI );

	public AppRole get( IRI appRoleIRI );

	public Set<AppRole> get( Agent agent );

	public Set<AppRole> get( App app, Agent agent );

	public void addAgent( IRI appRole, Agent agent );

	public Container createAppRolesContainer( IRI rootContainerIRI );

	public Set<IRI> getParentsIRI( IRI appRoleIRI );

	public IRI getContainerIRI();

	public IRI getAgentsContainerIRI( IRI appRoleIRI );

	public void delete( IRI appRoleIRI );
}
