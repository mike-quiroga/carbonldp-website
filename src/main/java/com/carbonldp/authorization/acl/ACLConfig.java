package com.carbonldp.authorization.acl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

@Configuration
public class ACLConfig extends GlobalMethodSecurityConfiguration {

	@Override
	protected MethodSecurityExpressionHandler createExpressionHandler() {
		DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
		handler.setPermissionEvaluator( permissionEvaluator() );

		return handler;
	}

	@Bean
	public ACLPermissionEvaluator permissionEvaluator() {
		return new ACLPermissionEvaluator(
			systemRoleACLPermissionVoter(),
			directACLPermissionVoter(),
			inheritanceACLPermissionVoter()
		);
	}

	@Bean
	public ACLPermissionVoter systemRoleACLPermissionVoter() {
		return new SystemRoleACLPermissionVoter();
	}

	@Bean( name = "directACLPermissionVoter" )
	public AbstractACLPermissionVoter directACLPermissionVoter() {
		return new DirectACLPermissionVoter();
	}

	@Bean( name = "inheritanceACLPermissionVoter" )
	public AbstractACLPermissionVoter inheritanceACLPermissionVoter() {
		return new InheritanceACLPermissionVoter();
	}
}
