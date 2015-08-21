package com.carbonldp.authorization.acl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

import javax.annotation.PostConstruct;

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
		return new ACLPermissionEvaluator(
			systemRoleACLPermissionVoter(),
			directACLPermissionVoter(),
			inheritanceACLPermissionVoter()
		);
	}

	/**
	 * PermissionEvaluator was being requested in early steps of bean initialization. Thus, Spring eagerly initialized it and its dependencies.
	 * directACLPermissionVoter needed the autowired field ACLRepository, but because of the eager initialization, it was getting a null value.
	 * This method solves that problem by injecting it after all the autowired is resolved.
	 * <p>
	 * TODO: Find who is causing this eager initialization and try to reorder the beans to solve the dependency cycle.
	 */
	@PostConstruct
	public void injectACLRepository() {
		( (AbstractACLPermissionVoter) directACLPermissionVoter() ).setACLRepository( aclRepository );
		( (AbstractACLPermissionVoter) inheritanceACLPermissionVoter() ).setACLRepository( aclRepository );
	}

	@Bean
	public ACLPermissionVoter systemRoleACLPermissionVoter() {
		return new SystemRoleACLPermissionVoter();
	}

	@Bean
	public ACLPermissionVoter directACLPermissionVoter() {
		return new DirectACLPermissionVoter();
	}

	@Bean
	public ACLPermissionVoter inheritanceACLPermissionVoter() {
		return new InheritanceACLPermissionVoter();
	}
}
