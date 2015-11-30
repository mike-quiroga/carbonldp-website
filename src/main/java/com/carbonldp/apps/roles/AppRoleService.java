package com.carbonldp.apps.roles;

import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Set;

/**
 * @author NstorVenegas
 * @since _version_
 */
public interface AppRoleService {

	@PreAuthorize( "hasPermission(#containerURI, 'ADD_MEMBER')" )
	public void addChildMembers( URI containerURI, Set<URI> members );

	public void addChildMember( URI containerURI, URI member );
}
