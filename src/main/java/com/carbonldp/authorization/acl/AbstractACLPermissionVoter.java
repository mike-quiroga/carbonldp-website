package com.carbonldp.authorization.acl;

import com.carbonldp.AbstractComponent;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.utils.ACLUtil;
import org.openrdf.model.URI;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractACLPermissionVoter extends AbstractComponent {
	protected ACLRepository aclRepository;

	public void setACLRepository( ACLRepository aclRepository ) {
		Assert.notNull( aclRepository );
		this.aclRepository = aclRepository;
	}

	protected Map<ACEDescription.Permission, Set<ACE>> getRelatedACEs( ACL acl, Map<RDFNodeEnum, Set<URI>> subjects, Set<ACEDescription.Permission> permissions ) {
		Map<ACEDescription.Permission, Set<ACE>> permissionsACEs = new HashMap<>();
		Set<ACE> aces = ACEFactory.get( acl, acl.getACEntries() );
		for ( ACE ace : aces ) {
			if ( ! ACLUtil.aceRefersToSubjects( ace, subjects ) ) continue;
			for ( ACEDescription.Permission permission : permissions ) {
				if ( ! ace.getPermissions().contains( permission ) ) continue;
				if ( ! permissionsACEs.containsKey( permission ) ) permissionsACEs.put( permission, new HashSet<>() );
				permissionsACEs.get( permission ).add( ace );
			}
		}
		return permissionsACEs;
	}
}
