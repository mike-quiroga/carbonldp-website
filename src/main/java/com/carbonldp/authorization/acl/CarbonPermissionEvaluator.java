package com.carbonldp.authorization.acl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import com.carbonldp.authorization.acl.ACEDescription.Permission;
import com.carbonldp.commons.descriptions.RDFNodeEnum;
import com.carbonldp.commons.models.URIObject;
import com.carbonldp.commons.utils.RDFNodeUtil;
import com.carbonldp.commons.utils.URIUtil;

public class CarbonPermissionEvaluator implements PermissionEvaluator {
	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		if ( targetDomainObject == null ) return false;

		URI objectURI = getObjectURI(targetDomainObject);

		return hasPermission(authentication, objectURI, permission);
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		// TODO: Process the targetID and the targetType to compose the representative OURI
		throw new NotImplementedException("IDs and types cannot be converted to a URI (yet).");
	}

	private boolean hasPermission(Authentication authentication, URI objectURI, Object permission) {
		List<Permission> permissions = resolvePermission(permission);
		List<URI> sURIs = SURIRetrievalStrategy.getSURIs(authentication);

		// TODO: Ask the appropiate service if the SURIs have the permission on the object
		return true;
	}

	private URI getObjectURI(Object targetDomainObject) {
		if ( targetDomainObject instanceof URI ) return (URI) targetDomainObject;
		if ( targetDomainObject instanceof URIObject ) return ((URIObject) targetDomainObject).getURI();

		// TODO: Support non URIObject objects (create/assign them one?)

		throw new IllegalArgumentException("Unsupported domain object: " + targetDomainObject);

	}

	private List<Permission> resolvePermission(Object permission) {
		if ( permission == null ) throw new IllegalArgumentException("The permission cannot be null.");

		if ( permission instanceof String ) return resolvePermission(new String[] { (String) permission });
		if ( permission instanceof String[] ) return resolvePermission((String[]) permission);
		if ( permission instanceof URI ) return resolvePermission(new URI[] { (URI) permission });
		if ( permission instanceof URI[] ) return resolvePermission((URI[]) permission);
		if ( permission instanceof Permission ) return Arrays.asList((Permission) permission);
		if ( permission instanceof Permission[] ) return Arrays.asList((Permission[]) permission);

		throw new IllegalArgumentException("Unsupported permission: " + permission);
	}

	private List<Permission> resolvePermission(String[] permissions) {
		List<Permission> list = new ArrayList<Permission>();
		for (String permission : permissions) {
			if ( URIUtil.isHTTPUri(permission) ) {
				return resolvePermission(new URIImpl(permission));
			} else {
				try {
					list.add(Permission.valueOf(permission));
				} catch (IllegalArgumentException e) {
					throw new IllegalArgumentException("The string: '" + permission + "', isn't related to a valid permission.", e);
				}
			}
		}
		return list;
	}

	private List<Permission> resolvePermission(URI[] permissionURIs) {
		List<Permission> list = new ArrayList<Permission>();
		for (URI permissionURI : permissionURIs) {
			RDFNodeEnum permissionEnum = RDFNodeUtil.findByURI(permissionURI, Permission.class);
			if ( permissionEnum != null ) {
				return Arrays.asList((Permission) permissionEnum);
			}
			throw new IllegalArgumentException("The URI: '" + permissionURI + "', isn't recognize as a valid permission.");
		}
		return list;
	}
}
