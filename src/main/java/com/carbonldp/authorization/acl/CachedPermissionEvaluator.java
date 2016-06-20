package com.carbonldp.authorization.acl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;

/**
 * Class in charge of caching permission evaluation. ACLPermissionEvaluator cannot use cache because
 * it is initialized without being wrapped with a Spring Proxy. Therefore any call made to it cannot
 * be intercepted by aspects.
 *
 * @author MiguelAraCo
 * @see <a href="https://jira.base22.com/browse/LDP-705">LDP-705</a>
 * @since 0.37.1
 */
public class CachedPermissionEvaluator {

	private ACLPermissionEvaluator permissionEvaluator;

	@Cacheable( "acl" )
	public boolean hasPermission( Authentication authentication, Object targetDomainObject, Object permission ) {
		return permissionEvaluator.resolveHasPermission( authentication, targetDomainObject, permission );
	}

	public void setPermissionEvaluator( ACLPermissionEvaluator permissionEvaluator ) {this.permissionEvaluator = permissionEvaluator;}
}
