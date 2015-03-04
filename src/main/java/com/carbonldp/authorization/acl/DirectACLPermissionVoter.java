package com.carbonldp.authorization.acl;

import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;

import com.carbonldp.authorization.acl.ACEDescription.Permission;
import com.carbonldp.descriptions.RDFNodeEnum;
import com.carbonldp.web.exceptions.NotImplementedException;

public class DirectACLPermissionVoter extends AbstractACLPermissionVoter implements ACLPermissionVoter {

	public DirectACLPermissionVoter(ACLRepository aclRepository) {
		super(aclRepository);
	}

	@Override
	public Vote vote(Map<RDFNodeEnum, Set<URI>> subjects, Set<Permission> permissions, URI objectURI) {
		// TODO: Implement
		throw new NotImplementedException();
	}

}
