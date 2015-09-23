package com.carbonldp.repository.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;

/**
 * @author MiguelAraCo
 * @since _version_
 */
@Configuration
public class RepositorySecurityConfig {
	@Autowired
	private PermissionEvaluator permissionEvaluator;

	@Bean
	public RepositorySecurityAccessGrantersHolder repositorySecurityAccessGrantersHolder() {
		return new RepositorySecurityAccessGrantersHolder(
			new RequestDomainAccessGranter(),
			aclAccessGranter()
		);
	}

	@Bean
	protected ACLAccessGranter aclAccessGranter() {
		return new ACLAccessGranter( permissionEvaluator );
	}
}
