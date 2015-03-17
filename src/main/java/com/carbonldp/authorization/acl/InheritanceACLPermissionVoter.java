package com.carbonldp.authorization.acl;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.utils.ACLUtil;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class InheritanceACLPermissionVoter extends AbstractACLPermissionVoter implements ACLPermissionVoter {
	private URI topParentURI;
	private URI platformsRootContainerURI;

	@Override
	public Vote vote( Map<RDFNodeEnum, Set<URI>> subjects, Set<ACEDescription.Permission> permissions, URI objectURI ) {
		URI topParentURI = getTopParentURI();
		List<URI> parentURIs = ACLUtil.getParentURIs( objectURI, topParentURI );
		for ( URI parentURI : parentURIs ) {
			ACL parentACL = aclRepository.getResourceACL( parentURI );
			// TODO: Evaluate ACL
		}
		return Vote.ABSTAIN;
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
