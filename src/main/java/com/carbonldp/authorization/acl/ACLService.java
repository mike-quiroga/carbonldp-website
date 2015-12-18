package com.carbonldp.authorization.acl;

import org.openrdf.model.URI;

public interface ACLService {

	public void replace( ACL acl );

	public ACL get( URI aclURI );
}
