package com.carbonldp.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

import com.carbonldp.authentication.AuthenticationConfig;
import com.carbonldp.authorization.AuthorizationConfig;
import com.carbonldp.authorization.acl.ACLConfig;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
//@formatter:off
@Import(
	value = {
		AuthenticationConfig.class,
		AuthorizationConfig.class,
		ACLConfig.class
	}
)
//@formatter:on
public class SecurityConfig {

}
