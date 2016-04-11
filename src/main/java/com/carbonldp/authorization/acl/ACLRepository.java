package com.carbonldp.authorization.acl;

import org.openrdf.model.IRI;

import java.util.Collection;

public interface ACLRepository {
	public ACL getResourceACL( IRI resourceIRI );

	public ACL createACL( IRI objectIRI );

	public void grantPermissions( IRI resourceIRI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable );

	public void grantPermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable );

	public void denyPermissions( IRI resourceIRI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable );

	public void denyPermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean inheritable );

	public void addInheritablePermissions( IRI resourceIRI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean granting );

	public void addInheritablePermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions, boolean granting );

	public void replace( ACL acl );

}
