package com.carbonldp.authorization.acl;

import com.carbonldp.AbstractComponent;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.PlatformRoleDescription;
import com.carbonldp.authorization.acl.ACEDescription.Permission;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.utils.RDFNodeUtil;
import org.openrdf.model.URI;

import java.util.Map;
import java.util.Set;

public class SystemRoleACLPermissionVoter extends AbstractComponent implements ACLPermissionVoter {

	@Override
	public Vote vote(Map<RDFNodeEnum, Set<URI>> subjects, Set<Permission> permissions, URI objectURI) {
		if ( !subjects.containsKey( PlatformRoleDescription.Resource.CLASS ) ) return Vote.ABSTAIN;
		Set<URI> platformRoleURIs = subjects.get( PlatformRoleDescription.Resource.CLASS );
		Set<Platform.Role> platformRoles = RDFNodeUtil.findByURIs( platformRoleURIs, Platform.Role.class );
		if ( platformRoles.contains( Platform.Role.SYSTEM ) ) return Vote.GRANT;
		return Vote.ABSTAIN;
	}
}
