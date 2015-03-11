package com.carbonldp.authorization.acl;

import com.carbonldp.authorization.acl.ACEDescription.Permission;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.rdf.URIObject;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.URIUtil;
import org.apache.commons.lang3.NotImplementedException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

public class ACLPermissionEvaluator implements PermissionEvaluator {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	private final List<ACLPermissionVoter> voters;

	public ACLPermissionEvaluator(ACLPermissionVoter... voters) {
		if ( voters.length <= 0 ) throw new IllegalArgumentException( "At least one voter needs to be provided." );
		List<ACLPermissionVoter> tempVoters = new ArrayList<ACLPermissionVoter>();
		for ( ACLPermissionVoter voter : voters ) {
			Assert.notNull( voter );
			tempVoters.add( voter );
		}
		this.voters = Collections.unmodifiableList( tempVoters );
	}

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		if ( targetDomainObject == null ) return false;

		URI objectURI = getObjectURI( targetDomainObject );

		return hasPermission( authentication, objectURI, permission );
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		// TODO: Process the targetID and the targetType to compose the representative OURI
		throw new NotImplementedException( "IDs and types cannot be converted to a URI (yet)." );
	}

	private boolean hasPermission(Authentication authentication, URI objectURI, Object permission) {
		Set<Permission> permissions = resolvePermissions( permission );
		Map<RDFNodeEnum, Set<URI>> subjects = SubjectsRetrievalStrategy.getSubjects( authentication );

		for ( ACLPermissionVoter voter : voters ) {
			switch ( voter.vote( subjects, permissions, objectURI ) ) {
				case GRANT:
					return true;
				case ABSTAIN:
					continue;
				case DENY:
					return false;
				default:
					throw new IllegalStateException();
			}
		}

		// TODO: Have default value
		return false;
	}

	private URI getObjectURI(Object targetDomainObject) {
		if ( targetDomainObject instanceof URI ) return (URI) targetDomainObject;
		if ( targetDomainObject instanceof URIObject ) return ((URIObject) targetDomainObject).getURI();

		// TODO: Support non URIObject objects (create/assign them one?)

		throw new IllegalArgumentException( "Unsupported domain object: " + targetDomainObject );

	}

	private Set<Permission> resolvePermissions(Object permission) {
		if ( permission == null ) throw new IllegalArgumentException( "The permission cannot be null." );

		if ( permission instanceof String ) return resolvePermissions( new String[]{(String) permission} );
		if ( permission instanceof String[] ) return resolvePermissions( (String[]) permission );
		if ( permission instanceof URI ) return resolvePermissions( new URI[]{(URI) permission} );
		if ( permission instanceof URI[] ) return resolvePermissions( (URI[]) permission );
		if ( permission instanceof Permission ) permission = new Permission[]{(Permission) permission};
		if ( permission instanceof Permission[] )
			return new HashSet<Permission>( Arrays.asList( (Permission[]) permission ) );
		;

		throw new IllegalArgumentException( "Unsupported permission: " + permission );
	}

	private Set<Permission> resolvePermissions(String[] permissionStrings) {
		Set<Permission> permissions = new HashSet<Permission>();
		for ( String permissionString : permissionStrings ) {
			Permission permission = null;
			if ( URIUtil.isHTTPUri( permissionString ) )
				permission = RDFNodeUtil.findByURI( new URIImpl( permissionString ), Permission.class );
			else permission = Permission.valueOf( permissionString );

			if ( permission == null ) throw new IllegalArgumentException( "Cannot recognize permission URI." );
			permissions.add( permission );
		}
		return permissions;
	}

	private Set<Permission> resolvePermissions(URI[] permissionURIs) {
		return RDFNodeUtil.findByURIs( Arrays.asList( permissionURIs ), Permission.class );
	}
}
