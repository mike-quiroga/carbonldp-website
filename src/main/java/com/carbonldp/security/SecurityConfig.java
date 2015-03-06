package com.carbonldp.security;

import com.carbonldp.authentication.AuthenticationConfig;
import com.carbonldp.authorization.AuthorizationConfig;
import com.carbonldp.authorization.acl.ACLConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@Configuration
@EnableGlobalMethodSecurity( prePostEnabled = true )
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
