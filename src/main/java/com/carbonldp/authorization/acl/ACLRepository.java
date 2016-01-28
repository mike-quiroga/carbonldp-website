package com.carbonldp.authorization.acl;

import org.openrdf.model.URI;

import java.util.Collection;

public interface ACLRepository {
	public ACL getResourceACL( URI resourceURI );

	public ACL createACL( URI objectURI );

	public void grantPermissions( URI resourceURI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable );

	public void grantPermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable );

	public void denyPermissions( URI resourceURI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable );

	public void denyPermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable );

	public void addInheritablePermissions( URI resourceURI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean granting );

	public void addInheritablePermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean granting );

	public void replace( ACL acl );

}
