package com.carbonldp.authorization.acl;

import com.carbonldp.authorization.acl.ACEDescription.Permission;
import com.carbonldp.rdf.IRIObject;
import com.carbonldp.rdf.RDFNodeEnum;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.RDFNodeUtil;
import org.apache.commons.lang3.NotImplementedException;
import org.openrdf.model.IRI;

import org.openrdf.model.impl.SimpleValueFactory;
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

	public ACLPermissionEvaluator( ACLPermissionVoter... voters ) {
		if ( voters.length <= 0 ) throw new IllegalArgumentException( "At least one voter needs to be provided." );
		List<ACLPermissionVoter> tempVoters = new ArrayList<ACLPermissionVoter>();
		for ( ACLPermissionVoter voter : voters ) {
			Assert.notNull( voter );
			tempVoters.add( voter );
		}
		this.voters = Collections.unmodifiableList( tempVoters );
	}

	@Override
	public boolean hasPermission( Authentication authentication, Object targetDomainObject, Object permission ) {
		if ( targetDomainObject == null ) return false;

		IRI objectIRI = getObjectIRI( targetDomainObject );

		return hasPermission( authentication, objectIRI, permission );
	}

	@Override
	public boolean hasPermission( Authentication authentication, Serializable targetId, String targetType, Object permission ) {
		// TODO: Process the targetID and the targetType to compose the representative OIRI
		throw new NotImplementedException( "IDs and types cannot be converted to a IRI (yet)." );
	}

	private boolean hasPermission( Authentication authentication, IRI objectIRI, Object permission ) {
		Set<Permission> permissions = resolvePermissions( permission );
		Map<RDFNodeEnum, Set<IRI>> subjects = SubjectsRetrievalStrategy.getSubjects( authentication );

		for ( ACLPermissionVoter voter : voters ) {
			// TODO: Implement PARTIAL Granting
			switch ( voter.vote( subjects, permissions, objectIRI ) ) {
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

	private IRI getObjectIRI( Object targetDomainObject ) {
		if ( targetDomainObject instanceof IRI ) return (IRI) targetDomainObject;
		if ( targetDomainObject instanceof IRIObject ) return ( (IRIObject) targetDomainObject ).getIRI();

		// TODO: Support non IRIObject objects (create/assign them one?)

		throw new IllegalArgumentException( "Unsupported domain object: " + targetDomainObject );

	}

	private Set<Permission> resolvePermissions( Object permission ) {
		if ( permission == null ) throw new IllegalArgumentException( "The permission cannot be null." );

		if ( permission instanceof String ) return resolvePermissions( new String[]{(String) permission} );
		if ( permission instanceof String[] ) return resolvePermissions( (String[]) permission );
		if ( permission instanceof IRI ) return resolvePermissions( new IRI[]{(IRI) permission} );
		if ( permission instanceof IRI[] ) return resolvePermissions( (IRI[]) permission );
		if ( permission instanceof Permission ) permission = new Permission[]{(Permission) permission};
		if ( permission instanceof Permission[] )
			return new HashSet<>( Arrays.asList( (Permission[]) permission ) );
		;

		throw new IllegalArgumentException( "Unsupported permission: " + permission );
	}

	private Set<Permission> resolvePermissions( String[] permissionStrings ) {
		Set<Permission> permissions = new HashSet<>();
		for ( String permissionString : permissionStrings ) {
			Permission permission;
			if ( IRIUtil.isHTTPIri( permissionString ) )
				permission = RDFNodeUtil.findByIRI( SimpleValueFactory.getInstance().createIRI( permissionString ), Permission.class );
			else permission = Permission.valueOf( permissionString );

			if ( permission == null ) throw new IllegalArgumentException( "Cannot recognize permission IRI." );
			permissions.add( permission );
		}
		return permissions;
	}

	private Set<Permission> resolvePermissions( IRI[] permissionIRIs ) {
		return RDFNodeUtil.findByIRIs( Arrays.asList( permissionIRIs ), Permission.class );
	}
}
