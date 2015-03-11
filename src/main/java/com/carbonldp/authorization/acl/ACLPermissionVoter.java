package com.carbonldp.authorization.acl;

import com.carbonldp.authorization.acl.ACEDescription.Permission;
import com.carbonldp.rdf.RDFNodeEnum;
import org.openrdf.model.URI;

import java.util.Map;
import java.util.Set;

public interface ACLPermissionVoter {
	public Vote vote(Map<RDFNodeEnum, Set<URI>> subjects, Set<Permission> permissions, URI objectURI);
}
