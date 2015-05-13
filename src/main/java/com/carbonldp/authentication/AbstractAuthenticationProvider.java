package com.carbonldp.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationProvider;

public abstract class AbstractAuthenticationProvider implements AuthenticationProvider {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
}
