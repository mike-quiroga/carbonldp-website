package com.base22.carbon.security.models;

import org.springframework.security.acls.domain.AbstractPermission;

import com.base22.carbon.security.models.CarbonACLPermissionFactory.CarbonPermission;

public class CarbonACLPermission extends AbstractPermission {

	private static final long serialVersionUID = - 2366581221528438493L;

	private CarbonPermission carbonPermission;

	public CarbonACLPermission(CarbonPermission carbonPermission) {
		super(carbonPermission.getMask());
		this.setCarbonPermission(carbonPermission);
	}

	public CarbonPermission getCarbonPermission() {
		return carbonPermission;
	}

	public void setCarbonPermission(CarbonPermission carbonPermission) {
		this.carbonPermission = carbonPermission;
	}
}