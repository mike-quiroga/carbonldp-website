package com.carbonldp.authorization.acl;

import com.carbonldp.authorization.acl.ACEDescription.Permission;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.utils.ACLUtil;
import com.carbonldp.utils.RDFResourceUtil;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.URI;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DirectACLPermissionVoter extends AbstractACLPermissionVoter implements ACLPermissionVoter {

	@Override
	public Vote vote( Map<RDFNodeEnum, Set<URI>> subjects, Set<Permission> permissions, URI objectURI ) {
		ACL acl = aclRepository.getResourceACL( objectURI );
		if ( acl == null || acl.isEmpty() ) return Vote.ABSTAIN;

		Map<Permission, ACE> permissionACE = new HashMap<>();
		Set<ACE> aces = RDFResourceUtil.getResourceViews( acl.getACEntries(), acl.getBaseModel(), ACE.class );
		for ( ACE ace : aces ) {
			if ( ! ACLUtil.aceRefersToSubjects( ace, subjects ) ) continue;
			for ( Permission permission : permissions ) {
				if ( ! ace.getPermissions().contains( permission ) ) continue;
				if ( permissionACE.containsKey( permission ) ) {
					// More than one ACE talks about the same permission
					// TODO: Handle subject class priority
					throw new IllegalStateException( new NotImplementedException( "Subject class priority hasn't been implemented." ) );
				}
				permissionACE.put( permission, ace );
			}
		}

		if ( permissionACE.isEmpty() ) return Vote.ABSTAIN;

		for ( ACE ace : permissionACE.values() ) {
			if ( ! ace.isGranting() ) return Vote.DENY;
		}

		if ( permissionACE.size() == permissions.size() ) return Vote.GRANT;

		// TODO: Handle partial granting
		return Vote.ABSTAIN;
	}
}
