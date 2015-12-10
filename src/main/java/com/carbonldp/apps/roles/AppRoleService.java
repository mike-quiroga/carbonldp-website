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

	public void create( AppRole appRole );

	public void addChildren( URI parentRole, Set<URI> childs );

	public void addChild( URI parentRole, URI child );
}
