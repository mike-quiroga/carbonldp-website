package com.carbonldp.authorization.acl;

import org.openrdf.model.URI;

public interface ACLRepository {
	public ACL getResourceACL( URI resourceURI );
}
