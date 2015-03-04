package com.carbonldp.authorization.acl;

import com.carbonldp.AbstractComponent;

public abstract class AbstractACLPermissionVoter extends AbstractComponent {
	protected final ACLRepository aclRepository;

	public AbstractACLPermissionVoter(ACLRepository aclRepository) {
		this.aclRepository = aclRepository;
	}
}
