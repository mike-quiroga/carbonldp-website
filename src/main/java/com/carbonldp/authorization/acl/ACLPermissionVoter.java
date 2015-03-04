package com.carbonldp.authorization.acl;

import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;

import com.carbonldp.authorization.acl.ACEDescription.Permission;
import com.carbonldp.descriptions.RDFNodeEnum;

public interface ACLPermissionVoter {
	public Vote vote(Map<RDFNodeEnum, Set<URI>> subjects, Set<Permission> permissions, URI objectURI);
}
