package com.carbonldp.authorization.acl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
public class ACLConfig extends GlobalMethodSecurityConfiguration {

	@Autowired
	private ACLRepository aclRepository;

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setPermissionEvaluator( permissionEvaluator() );

		return handler;
	}

	@Bean
	public PermissionEvaluator permissionEvaluator() {
		//@formatter:off
		return new ACLPermissionEvaluator(
				systemRoleACLPermissionVoter(),
				directACLPermissionVoter()
		);
		//@formatter:on
	}

	@Bean
	public ACLPermissionVoter systemRoleACLPermissionVoter() {
		return new SystemRoleACLPermissionVoter();
	}

	@Bean
	public ACLPermissionVoter directACLPermissionVoter() {
		return new DirectACLPermissionVoter( aclRepository );
	}
}
