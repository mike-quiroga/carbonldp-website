package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRole;
import org.openrdf.model.URI;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since 0.18.0-ALPHA
 */
public interface AppRoleService {

	public boolean exists( URI appURI );

	public void addAgents( URI targetUri, Set<URI> agents );

	public void create( AppRole appRole );

	public void addChildren( URI parentRole, Set<URI> childs );

	public void addAgent( URI appRole, URI agent );

	public void addChild( URI parentRole, URI child );

	public void delete( URI appURI );

	public void removeAgentMembers( URI appRoleAgentContainerURI, Set<URI> agents );

	public void removeAgentMember( URI appRoleAgentContainerURI, URI agent );
}
