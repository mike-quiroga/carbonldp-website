package com.carbonldp.authorization.acl;

import com.carbonldp.AbstractComponent;
import org.springframework.util.Assert;

public abstract class AbstractACLPermissionVoter extends AbstractComponent implements ACLPermissionVoter {
	protected ACLRepository aclRepository;

	public void setACLRepository( ACLRepository aclRepository ) {
		Assert.notNull( aclRepository );
		this.aclRepository = aclRepository;
	}
}
