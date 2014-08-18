package com.base22.carbon.security.models;

import org.springframework.security.acls.domain.AbstractPermission;

public class PermissionImpl extends AbstractPermission {

	private static final long serialVersionUID = - 2666355029236840446L;

	public PermissionImpl(int mask) {
		super(mask);
	}

}
