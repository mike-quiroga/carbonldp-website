package com.carbonldp.security;

import com.carbonldp.authentication.AuthenticationConfig;
import com.carbonldp.authorization.AuthorizationConfig;
import com.carbonldp.authorization.acl.ACLConfig;
import com.carbonldp.repository.security.RepositorySecurityConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity( prePostEnabled = true )
@Import(
	value = {
		ACLConfig.class,
		AuthenticationConfig.class,
		AuthorizationConfig.class,
		RepositorySecurityConfig.class
	}
)
public class SecurityConfig {

}
