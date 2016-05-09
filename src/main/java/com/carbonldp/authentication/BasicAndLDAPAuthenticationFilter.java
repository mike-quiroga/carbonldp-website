package com.carbonldp.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * @author NestorVenegas
 * @author JorgeEspinosa
 * @since _version_
 */
public class BasicAndLDAPAuthenticationFilter extends BasicAuthenticationFilter {

	public BasicAndLDAPAuthenticationFilter( AuthenticationManager authenticationManager ) {
		super( authenticationManager );
	}

	public BasicAndLDAPAuthenticationFilter( AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint ) {
		super( authenticationManager, authenticationEntryPoint );
	}


}
