package com.base22.carbon.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.AuditableAccessControlEntry;
import org.springframework.security.acls.model.Permission;

import com.base22.carbon.security.models.CarbonACLPermission;

public class CarbonAuditLogger implements AuditLogger {

	protected final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Override
	public void logIfNeeded(boolean granted, AccessControlEntry ace) {
		// Doesn't provide enough info, use the one below
	}

	public void logIfNeeded(Permission permission, boolean granted, AccessControlEntry ace) {
		if ( ace instanceof AuditableAccessControlEntry ) {
			AuditableAccessControlEntry auditableAce = (AuditableAccessControlEntry) ace;
			logAuditableACE(permission, granted, auditableAce);
		}
	}

	public void logAuditableACE(Permission permission, boolean granted, AuditableAccessControlEntry ace) {
		if ( permission instanceof CarbonACLPermission ) {
			CarbonACLPermission carbonPermission = (CarbonACLPermission) permission;
			logCarbonPermission(carbonPermission, granted, ace);
		}
	}

	public void logCarbonPermission(CarbonACLPermission permission, boolean granted, AuditableAccessControlEntry ace) {
		if ( granted && ace.isAuditSuccess() ) {
			if ( LOG.isInfoEnabled() ) {
				LOG.info("<< logCarbonPermission() > GRANTED Permission: '{}', on Sid: '{}'", permission.getCarbonPermission().name(), ace.getSid().toString());
			}
		} else if ( ! granted && ace.isAuditFailure() ) {
			if ( LOG.isInfoEnabled() ) {
				LOG.info("<< logCarbonPermission() > DENIED Permission: '{}', on Sid: '{}'", permission.getCarbonPermission().name(), ace.getSid().toString());
			}
		}
	}
}
