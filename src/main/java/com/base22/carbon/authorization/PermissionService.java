package com.base22.carbon.authorization;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.ChildrenExistException;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.base22.carbon.CarbonException;

@Service("permissionService")
public class PermissionService {

	@Autowired
	private AclCache aclCache;
	@Autowired
	private PlatformTransactionManager transactionManager;
	@Autowired
	private MutableAclService aclService;

	static final Logger LOG = LoggerFactory.getLogger(PermissionService.class);

	public boolean hasPermission(Permission permission, Object object) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		List<Sid> sids = new ArrayList<Sid>();

		sids.add(new PrincipalSid(authentication));
		for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
			sids.add(new GrantedAuthoritySid(grantedAuthority));
		}

		return hasPermission(sids, permission, object);
	}

	public boolean hasPermission(final List<Sid> sids, final Permission permission, Object object) {
		boolean hasIt = false;

		final ObjectIdentity oi = new ObjectIdentityImpl(object);

		TransactionTemplate tt = new TransactionTemplate(transactionManager);
		hasIt = tt.execute(new TransactionCallback<Boolean>() {
			@Override
			public Boolean doInTransaction(TransactionStatus status) {
				MutableAcl acl = null;
				try {
					acl = (MutableAcl) aclService.readAclById(oi);
				} catch (NotFoundException nfe) {
					acl = aclService.createAcl(oi);
				}

				List<Permission> permissions = new ArrayList<Permission>();
				permissions.add(permission);

				return acl.isGranted(permissions, sids, false);
			}
		});

		return hasIt;
	}

	public void grantPermission(Permission permission, Object object) throws CarbonException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Sid sid = new PrincipalSid(authentication);

		grantPermission(sid, permission, object);
	}

	public void grantPermission(final Sid sid, final Permission permission, final Object object) throws CarbonException {
		grantPermissions(sid, object, Arrays.asList(permission));
	}

	public void grantPermissions(final Sid sid, final Object object, final List<? extends Permission> permissions) throws CarbonException {
		final ObjectIdentity oi = new ObjectIdentityImpl(object);

		TransactionTemplate tt = new TransactionTemplate(transactionManager);
		tt.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {

				MutableAcl acl = null;
				try {
					acl = (MutableAcl) aclService.readAclById(oi);
				} catch (NotFoundException nfe) {
					acl = aclService.createAcl(oi);
				}

				int permissionsMask = 0;
				for (Permission permissionToCombine : permissions) {
					permissionsMask = permissionsMask | permissionToCombine.getMask();
				}
				Permission permission = new PermissionImpl(permissionsMask);

				List<AccessControlEntry> aces = acl.getEntries();

				boolean granted = false;
				for (AccessControlEntry ace : aces) {
					boolean deleteEntry = false;
					Permission permissionToInsert = null;
					boolean insertGranting = false;

					if ( ace.isGranting() ) {
						if ( ! bitwiseCheckPermissionPrescence(permission, ace) ) {
							deleteEntry = true;
							permissionToInsert = bitwiseCombinePermissions(permission, ace.getPermission());
							insertGranting = true;
						}
						granted = true;
					} else {
						if ( bitwiseCheckPermissionPrescence(permission, ace) ) {
							// Delete the entry and recreate it without the permission mask
							deleteEntry = true;
							permissionToInsert = bitwiseRemovePermissions(permission, ace.getPermission());
							insertGranting = false;
							if ( permissionToInsert.getMask() == permission.getMask() ) {
								permissionToInsert = null;
							}
						}
					}

					if ( deleteEntry ) {
						acl.deleteAce(acl.getEntries().indexOf(ace));
					}
					if ( permissionToInsert != null ) {
						acl.insertAce(acl.getEntries().size(), permissionToInsert, sid, insertGranting);
					}
				}
				if ( ! granted ) {
					acl.insertAce(acl.getEntries().size(), permission, sid, true);
				}

				aclService.updateAcl(acl);
			}
		});
	}

	public void denyPermission(Permission permission, Object object) throws CarbonException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Sid sid = new PrincipalSid(authentication);

		denyPermission(sid, object, permission);
	}

	public void denyPermission(final Sid sid, Object object, final Permission permission) throws CarbonException {
		denyPermissions(sid, object, Arrays.asList(permission));
	}

	public void denyPermissions(final Sid sid, Object object, final List<? extends Permission> permissions) throws CarbonException {
		final ObjectIdentity oi = new ObjectIdentityImpl(object);

		TransactionTemplate tt = new TransactionTemplate(transactionManager);
		tt.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				MutableAcl acl = null;
				try {
					acl = (MutableAcl) aclService.readAclById(oi);
				} catch (NotFoundException nfe) {
					acl = aclService.createAcl(oi);
				}

				int permissionsMask = 0;
				for (Permission permissionToCombine : permissions) {
					permissionsMask = permissionsMask | permissionToCombine.getMask();
				}
				Permission permission = new PermissionImpl(permissionsMask);

				List<AccessControlEntry> aces = acl.getEntries();

				boolean denied = false;
				for (AccessControlEntry ace : aces) {
					boolean deleteEntry = false;
					Permission permissionToInsert = null;
					boolean insertGranting = false;

					if ( ace.isGranting() ) {
						if ( bitwiseCheckPermissionPrescence(permission, ace) ) {
							// Delete the entry and recreate it without the permission mask
							deleteEntry = true;
							permissionToInsert = bitwiseRemovePermissions(permission, ace.getPermission());
							insertGranting = true;
							// Check if the entry contained only this permission mask
							if ( permissionToInsert.getMask() == permission.getMask() ) {
								// It did, don't insert it again
								permissionToInsert = null;
							}
						}
					} else {
						if ( ! bitwiseCheckPermissionPrescence(permission, ace) ) {
							deleteEntry = true;
							permissionToInsert = bitwiseCombinePermissions(permission, ace.getPermission());
							insertGranting = false;
						}
						denied = true;
					}

					if ( deleteEntry ) {
						acl.deleteAce(acl.getEntries().indexOf(ace));
					}
					if ( permissionToInsert != null ) {
						acl.insertAce(acl.getEntries().size(), permissionToInsert, sid, insertGranting);
					}
				}
				if ( ! denied ) {
					acl.insertAce(acl.getEntries().size(), permission, sid, false);
				}

				aclService.updateAcl(acl);
			}
		});
	}

	public void setParent(Object child, Object parent) throws CarbonException {
		final ObjectIdentity childObjectIdentity = new ObjectIdentityImpl(child);
		final ObjectIdentity parentObjectIdentity = new ObjectIdentityImpl(parent);

		TransactionTemplate tt = new TransactionTemplate(transactionManager);
		tt.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				MutableAcl childACL = null;
				try {
					childACL = (MutableAcl) aclService.readAclById(childObjectIdentity);
				} catch (NotFoundException nfe) {
					childACL = aclService.createAcl(childObjectIdentity);
				}
				MutableAcl parentACL = null;
				try {
					parentACL = (MutableAcl) aclService.readAclById(parentObjectIdentity);
				} catch (NotFoundException nfe) {
					parentACL = aclService.createAcl(parentObjectIdentity);
				}

				childACL.setParent(parentACL);

				aclService.updateAcl(parentACL);
				aclService.updateAcl(childACL);
			}
		});
	}

	public void setParentToMany(final List<? extends Object> children, Object parent) throws CarbonException {
		final ObjectIdentity parentObjectIdentity = new ObjectIdentityImpl(parent);

		TransactionTemplate tt = new TransactionTemplate(transactionManager);
		tt.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				MutableAcl parentACL = null;
				try {
					parentACL = (MutableAcl) aclService.readAclById(parentObjectIdentity);
				} catch (NotFoundException nfe) {
					parentACL = aclService.createAcl(parentObjectIdentity);
				}

				for (Object child : children) {
					ObjectIdentity childObjectIdentity = new ObjectIdentityImpl(child);

					MutableAcl childACL = null;
					try {
						childACL = (MutableAcl) aclService.readAclById(childObjectIdentity);
					} catch (NotFoundException nfe) {
						childACL = aclService.createAcl(childObjectIdentity);
					}

					childACL.setParent(parentACL);

					aclService.updateAcl(childACL);
				}
			}
		});
	}

	public void emptyACL(Object object) throws CarbonException {
		final ObjectIdentity objectIdentity = new ObjectIdentityImpl(object);

		try {
			TransactionTemplate tt = new TransactionTemplate(transactionManager);
			tt.execute(new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(TransactionStatus status) {
					MutableAcl acl = (MutableAcl) aclService.readAclById(objectIdentity);

					int aclEntries = acl.getEntries().size();
					for (int i = 0; i < aclEntries; i++) {
						acl.deleteAce(i);
					}
					aclService.updateAcl(acl);
				}
			});
		} catch (NotFoundException e) {
			// The ACL was already empty
		} catch (Exception e) {
			// TODO: FT
			throw new CarbonException("Couldn't empty the acl of the object.");
		}
	}

	// TODO: Handle Exceptions
	public void deleteACL(Object object, final boolean deleteChildren) throws CarbonException {
		final ObjectIdentity objectIdentity = new ObjectIdentityImpl(object);

		TransactionTemplate tt = new TransactionTemplate(transactionManager);
		tt.execute(new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				try {
					aclService.deleteAcl(objectIdentity, deleteChildren);
				} catch (ChildrenExistException nfe) {
					return;
				}
			}
		});
	}

	public Acl getACL(Object object) throws CarbonException {
		Acl acl = null;

		final ObjectIdentity oi = new ObjectIdentityImpl(object);

		try {
			TransactionTemplate tt = new TransactionTemplate(transactionManager);
			acl = tt.execute(new TransactionCallback<Acl>() {
				@Override
				public Acl doInTransaction(TransactionStatus status) {
					Acl acl = null;
					try {
						acl = aclService.readAclById(oi);
					} catch (NotFoundException nfe) {
						acl = aclService.createAcl(oi);
					}
					return acl;
				}
			});
		} catch (Exception e) {
			// TODO: FT
		}

		return acl;
	}

	private boolean bitwiseCheckPermissionPrescence(Permission permission, AccessControlEntry ace) {
		return (ace.getPermission().getMask() & permission.getMask()) == permission.getMask();
	}

	private Permission bitwiseCombinePermissions(Permission permission, Permission acePermission) {
		return new PermissionImpl(permission.getMask() | acePermission.getMask());
	}

	private Permission bitwiseRemovePermissions(Permission permissionsToBeRemoved, Permission fromPermissions) {
		return new PermissionImpl(~ ((~ fromPermissions.getMask()) | permissionsToBeRemoved.getMask()));
	}

	public void setAclCache(AclCache aclCache) {
		this.aclCache = aclCache;
	}

	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setAclService(MutableAclService aclService) {
		this.aclService = aclService;
	}
}
