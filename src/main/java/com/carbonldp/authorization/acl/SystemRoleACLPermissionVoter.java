package com.carbonldp.authorization.acl;

import com.carbonldp.AbstractComponent;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.PlatformRoleDescription;
import com.carbonldp.authorization.acl.ACEDescription.Permission;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.utils.RDFNodeUtil;
import org.openrdf.model.IRI;

import java.util.Map;
import java.util.Set;

public class SystemRoleACLPermissionVoter extends AbstractComponent implements ACLPermissionVoter {

	@Override
	public Vote vote( Map<RDFNodeEnum, Set<IRI>> subjects, Set<Permission> permissions, IRI objectIRI ) {
		if ( ! subjects.containsKey( PlatformRoleDescription.Resource.CLASS ) ) return Vote.ABSTAIN;
		Set<IRI> platformRoleIRIs = subjects.get( PlatformRoleDescription.Resource.CLASS );
		Set<Platform.Role> platformRoles = RDFNodeUtil.findByIRIs( platformRoleIRIs, Platform.Role.class );
		if ( platformRoles.contains( Platform.Role.SYSTEM ) ) return Vote.GRANT;
		return Vote.ABSTAIN;
	}
}
