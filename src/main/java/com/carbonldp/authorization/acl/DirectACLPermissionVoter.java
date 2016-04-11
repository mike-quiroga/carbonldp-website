package com.carbonldp.authorization.acl;

import com.carbonldp.authorization.acl.ACEDescription.Permission;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.utils.ACLUtil;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.IRI;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DirectACLPermissionVoter extends AbstractACLPermissionVoter implements ACLPermissionVoter {

	@Override
	public Vote vote( Map<RDFNodeEnum, Set<IRI>> subjects, Set<Permission> permissions, IRI objectIRI ) {
		ACL acl = aclRepository.getResourceACL( objectIRI );
		if ( acl == null || acl.isEmpty() ) return Vote.ABSTAIN;

		Map<Permission, ACE> permissionACE = new HashMap<>();
		Set<ACE> aces = ACEFactory.getInstance().get( acl, acl.getACEntries() );
		for ( ACE ace : aces ) {
			if ( ! ACLUtil.aceRefersToSubjects( ace, subjects ) ) continue;
			for ( Permission permission : permissions ) {
				Set<Permission> acePermissions = ace.getPermissions();
				if ( ! acePermissions.contains( permission ) ) continue;
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
