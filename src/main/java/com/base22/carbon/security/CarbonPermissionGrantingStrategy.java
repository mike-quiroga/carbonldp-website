package com.base22.carbon.security;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.acls.model.Sid;
import org.springframework.util.Assert;

import com.base22.carbon.security.models.CarbonACLPermission;

public class CarbonPermissionGrantingStrategy implements PermissionGrantingStrategy {
	private final transient AuditLogger auditLogger;

	@Autowired
	private AclCache aclCache;

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	/**
	 * Creates an instance with the logger which will be used to record granting and denial of requested permissions.
	 */
	public CarbonPermissionGrantingStrategy(AuditLogger auditLogger) {
		Assert.notNull(auditLogger, "auditLogger cannot be null");
		this.auditLogger = auditLogger;
	}

	/**
	 * Determines authorization. The order of the <code>permission</code> and <code>sid</code> arguments is
	 * <em>extremely important</em>! The method will iterate through each of the <code>permission</code>s in the order
	 * specified. For each iteration, all of the <code>sid</code>s will be considered, again in the order they are
	 * presented. A search will then be performed for the first {@link AccessControlEntry} object that directly matches
	 * that <code>permission:sid</code> combination. When the <em>first full match</em> is found (ie an ACE that has the
	 * SID currently being searched for and the exact permission bit mask being search for), the grant or deny flag for
	 * that ACE will prevail. If the ACE specifies to grant access, the method will return <code>true</code>. If the ACE
	 * specifies to deny access, the loop will stop and the next <code>permission</code> iteration will be performed. If
	 * each permission indicates to deny access, the first deny ACE found will be considered the reason for the failure
	 * (as it was the first match found, and is therefore the one most logically requiring changes - although not
	 * always). If absolutely no matching ACE was found at all for any permission, the parent ACL will be tried
	 * (provided that there is a parent and {@link Acl#isEntriesInheriting()} is <code>true</code>. The parent ACL will
	 * also scan its parent and so on. If ultimately no matching ACE is found, a <code>NotFoundException</code> will be
	 * thrown and the caller will need to decide how to handle the permission check. Similarly, if any of the SID
	 * arguments presented to the method were not loaded by the ACL, <code>UnloadedSidException</code> will be thrown.
	 * 
	 * @param permissions
	 *            the exact permissions to scan for (order is important)
	 * @param sids
	 *            the exact SIDs to scan for (order is important)
	 * @param administrativeMode
	 *            if <code>true</code> denotes the query is for administrative purposes and no auditing will be
	 *            undertaken
	 * 
	 * @return <code>true</code> if one of the permissions has been granted, <code>false</code> if one of the
	 *         permissions has been specifically revoked
	 * 
	 * @throws NotFoundException
	 *             if an exact ACE for one of the permission bit masks and SID combination could not be found
	 */
	public boolean isGranted(Acl acl, List<Permission> permissions, List<Sid> sids, boolean administrativeMode) throws NotFoundException {

		// TODO: Remove this
		// Temporary Hack to debug
		aclCache.clearCache();

		final List<AccessControlEntry> aces = acl.getEntries();

		Boolean hasPermission = null;
		for (Permission permission : permissions) {
			hasPermission = checkPermission(permission, sids, aces);
			if ( hasPermission != null ) {
				break;
			}
			// The permission wasn't found
		}

		if ( hasPermission != null ) {
			return hasPermission;
		}

		// The permissions were not found on this acl
		// Inheritance
		// Search parent ACL
		hasPermission = checkParentACL(acl, permissions, sids);

		if ( hasPermission != null ) {
			return hasPermission;
		}

		// The permissions were not found on this acl or its parent
		// Defaults
		// TODO: Take into account defaults
		if ( LOG.isDebugEnabled() ) {
			LOG.debug("<< isGranted() > NOT FOUND");
		}
		return false;
	}

	private Boolean checkPermission(Permission permission, List<Sid> sids, List<AccessControlEntry> aces) {
		for (Sid sid : sids) {
			Boolean hasPermission = null;
			hasPermission = checkSid(permission, sid, aces);
			if ( hasPermission != null ) {
				return hasPermission;
			} else {
				// The permission wasn't found on the Sid
			}
		}
		// The permission wasn't found
		return null;
	}

	private Boolean checkSid(Permission permission, Sid sid, List<AccessControlEntry> aces) {
		for (AccessControlEntry ace : aces) {
			Boolean hasPermission = null;
			hasPermission = checkACE(permission, sid, ace);
			if ( hasPermission != null ) {
				return hasPermission;
			} else {
				// The permission wasn't found on the Sid
			}
		}
		// The permission wasn't found
		return null;
	}

	private Boolean checkACE(Permission permission, Sid sid, AccessControlEntry ace) {
		if ( ((ace.getPermission().getMask() & permission.getMask()) == permission.getMask()) && ace.getSid().equals(sid) ) {
			// Found a matching ACE, so its authorization decision will prevail
			boolean granted = ace.isGranting();

			if ( LOG.isDebugEnabled() ) {
				String permissionName = null;
				if ( permission instanceof CarbonACLPermission ) {
					permissionName = ((CarbonACLPermission) permission).getCarbonPermission().name();
				} else {
					permissionName = permission.toString();
				}

				if ( granted ) {
					LOG.debug("<< checkACE() > GRANTED Permission: '{}', on Sid: '{}'", permissionName, ace.getSid().toString());
				} else {
					LOG.debug("<< checkACE() > GRANTED Permission: '{}', on Sid: '{}'", permissionName, ace.getSid().toString());
				}
			}

			return granted;
		}
		// The permission wasn't found
		return null;
	}

	private Boolean checkParentACL(Acl acl, List<Permission> permissions, List<Sid> sids) {
		Boolean hasPermission = null;
		if ( acl.isEntriesInheriting() && (acl.getParentAcl() != null) ) {
			hasPermission = acl.getParentAcl().isGranted(permissions, sids, false);
		}
		return hasPermission;
	}

	public void setAclCache(AclCache aclCache) {
		this.aclCache = aclCache;
	}
}
