package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRole;
import org.openrdf.model.URI;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since _version_
 */
public interface AppRoleService {
	public boolean exists( URI appRoleURI );

	public void create( AppRole appRole );

	public void addChildMembers( URI parentRole, Set<URI> childs );

	public void addChildMember( URI parentRole, URI child );
}
