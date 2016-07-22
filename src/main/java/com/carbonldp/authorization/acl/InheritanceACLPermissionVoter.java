package com.carbonldp.authorization.acl;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.utils.ACLUtil;
import com.carbonldp.web.exceptions.NotImplementedException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InheritanceACLPermissionVoter extends AbstractACLPermissionVoter {

	@Override
	public Vote vote( Map<RDFNodeEnum, Set<IRI>> subjects, Set<ACEDescription.Permission> permissions, IRI objectIRI ) {
		Set<ACEDescription.Permission> permissionsToGrant = new HashSet<>();
		permissionsToGrant.addAll( permissions );

		IRI topParentIRI = getTopParentIRI();
		List<IRI> parentIRIs = ACLUtil.getParentIRIs( objectIRI, topParentIRI );
		for ( IRI parentIRI : parentIRIs ) {
			ACL parentACL = aclRepository.getResourceACL( parentIRI );
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

	private IRI getTopParentIRI() {
		if ( AppContextHolder.getContext().isEmpty() ) return getPlatformsRootContainerIRI();
		else return getAppRootContainerIRI();
	}

	private IRI getPlatformsRootContainerIRI() {
		return SimpleValueFactory.getInstance().createIRI( Vars.getInstance().getMainContainerURL() );
	}

	private IRI getAppRootContainerIRI() {
		App app = AppContextHolder.getContext().getApplication();
		return app.getRootContainerIRI();
	}
}
