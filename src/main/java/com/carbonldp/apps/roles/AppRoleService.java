package com.carbonldp.apps.roles;

import org.openrdf.model.URI;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since _version_
 */

public interface AppRoleService {

	public void addAgentMembers( URI targetUri, Set<URI> agents );

	public void addAgentMember( URI appRole, URI agent );
}
