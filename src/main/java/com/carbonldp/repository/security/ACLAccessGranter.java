package com.carbonldp.repository.security;

import com.carbonldp.authorization.acl.ACEDescription;
import org.openrdf.model.Statement;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class ACLAccessGranter implements RepositorySecurityAccessGranter {

	private final PermissionEvaluator permissionEvaluator;
	private Authentication authentication;

	public ACLAccessGranter( PermissionEvaluator permissionEvaluator ) {
		this.permissionEvaluator = permissionEvaluator;
	}

	@Override
	public Vote canAccess( Statement statement ) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( authentication == null ) return Vote.ABSTAIN;
		if ( permissionEvaluator.hasPermission( authentication, statement.getContext(), ACEDescription.Permission.READ ) ) return Vote.GRANT;
		else return Vote.DENY;
	}
}
