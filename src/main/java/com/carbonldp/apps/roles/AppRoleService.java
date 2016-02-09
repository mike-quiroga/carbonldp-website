package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRole;
import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since 0.18.0-ALPHA
 */
public interface AppRoleService {

	public boolean exists( URI appURI );

	public void addAgents( URI appRoleAgentContainerURI, Set<URI> agents );

	public void create( AppRole appRole );

	public void addChildren( URI parentRole, Set<URI> childs );

	public void addAgent( URI appRoleAgentConatinerURI, URI agent );

	public void addChild( URI parentRole, URI child );

	@PreAuthorize( "hasPermission(#appRoleURI, 'DELETE')" )
	public void delete( URI appRoleURI );
}
