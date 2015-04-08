package com.carbonldp.authorization;

import com.carbonldp.apps.context.AppContextPersistenceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

import javax.servlet.Filter;

public abstract class AbstractWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
	@Autowired
	@Qualifier( "basicAuthenticationFilter" )
	protected Filter basicAuthenticationFilter;

	@Autowired
	@Qualifier( "basicAuthenticationEntryPoint" )
	protected AuthenticationEntryPoint basicAuthenticationEntryPoint;

	@Autowired
	@Qualifier( "appRolePersistenceFilter" )
	protected Filter appRolePersistenceFilter;

	@Autowired
	protected AppContextPersistenceFilter appContextPersistenceFilter;

	protected AbstractWebSecurityConfigurerAdapter() {
		super( true );
	}

	@Override
	protected void configure( HttpSecurity http ) throws Exception {
		http
			// TODO: Protect against CSRF using another method
			// .csrf().and()
			.addFilter( new WebAsyncManagerIntegrationFilter() )
			.headers().and()
			.sessionManagement().and()
			.securityContext().and()
			.anonymous().and()
			.servletApi()
		;
	}
}
