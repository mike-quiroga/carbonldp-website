package com.carbonldp.authorization.acl;

import com.carbonldp.rdf.RDFDocument;
import org.openrdf.model.URI;

import java.util.Collection;

public interface ACLRepository {
	public ACL getResourceACL( URI resourceURI );

	public ACL createACL( RDFDocument objectSource );

	public void grantPermissions( URI resourceURI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions );

	public void grantPermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions );

	public void denyPermissions( URI resourceURI, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions );

	public void denyPermissions( ACL acl, Collection<ACLSubject> subjects, Collection<ACEDescription.Permission> permissions );
}
