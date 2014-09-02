package com.base22.carbon.authorization;

import org.springframework.security.acls.domain.DefaultPermissionFactory;

import com.base22.carbon.authorization.acl.CarbonACLPermission;

public class CarbonPermissionFactory extends DefaultPermissionFactory {
	public CarbonPermissionFactory() {
		registerPublicPermissions(CarbonACLPermission.class);
	}
}
