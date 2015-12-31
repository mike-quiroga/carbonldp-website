package com.carbonldp.authorization.acl;

import org.openrdf.model.URI;

public interface ACLService {

	// TODO: implement security
	public void replace( ACL acl );

	public ACL get( URI aclURI );
}
