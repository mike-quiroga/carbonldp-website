package com.base22.carbon.security.factories;

import org.springframework.security.acls.domain.DefaultPermissionFactory;

import com.base22.carbon.security.models.CarbonACLPermission;

public class CarbonPermissionFactory extends DefaultPermissionFactory {
	public CarbonPermissionFactory() {
		registerPublicPermissions(CarbonACLPermission.class);
	}
}
