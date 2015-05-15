package com.carbonldp.authorization.acl;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.utils.ACLUtil;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InheritanceACLPermissionVoter extends AbstractACLPermissionVoter implements ACLPermissionVoter {

	@Override
	public Vote vote( Map<RDFNodeEnum, Set<URI>> subjects, Set<ACEDescription.Permission> permissions, URI objectURI ) {
		Set<ACEDescription.Permission> permissionsToGrant = new HashSet<>();
		permissionsToGrant.addAll( permissions );

		URI topParentURI = getTopParentURI();
		List<URI> parentURIs = ACLUtil.getParentURIs( objectURI, topParentURI );
		for ( URI parentURI : parentURIs ) {
			ACL parentACL = aclRepository.getResourceACL( parentURI );
			if ( parentACL == null || parentACL.isEmpty() ) continue;

			Map<ACEDescription.Permission, Set<ACE>> permissionsACEs = ACLUtil.getRelatedInheritableACEs( parentACL, subjects, permissionsToGrant );
			if ( permissionsACEs.isEmpty() ) continue;

			for ( ACEDescription.Permission acePermission : permissionsACEs.keySet() ) {
				Set<ACE> permissionACEs = permissionsACEs.get( acePermission );
				if ( permissionACEs.size() != 1 ) throw new IllegalStateException( new NotImplementedException( "Subject class priority hasn't been implemented." ) );
				ACE permissionACE = permissionACEs.iterator().next();
				if ( ! permissionACE.isGranting() ) return Vote.DENY;
				permissionsToGrant.remove( acePermission );
			}

			if ( permissionsToGrant.isEmpty() ) break;
		}

		if ( permissionsToGrant.isEmpty() ) return Vote.GRANT;
		else return Vote.ABSTAIN;
	}

	private URI getTopParentURI() {
		if ( AppContextHolder.getContext().isEmpty() ) return getPlatformsRootContainerURI();
		else return getAppRootContainerURI();
	}

	private URI getPlatformsRootContainerURI() {
		return new URIImpl( Vars.getMainContainerURL() );
	}

	private URI getAppRootContainerURI() {
		App app = AppContextHolder.getContext().getApplication();
		return app.getRootContainerURI();
	}
}
