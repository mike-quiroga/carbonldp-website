package com.carbonldp.authorization;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;

import com.carbonldp.apps.context.AppContextPersistanceFilter;

public abstract class AbstractWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
	@Autowired
	@Qualifier("basicAuthenticationFilter")
	protected Filter basicAuthenticationFilter;
	@Autowired
	@Qualifier("basicAuthenticationEntryPoint")
	protected AuthenticationEntryPoint basicAuthenticationEntryPoint;

	protected AppContextPersistanceFilter appContextPersistanceFilter;

	protected AbstractWebSecurityConfigurerAdapter() {
		super(true);
	}

	@Autowired
	public void setAppContextPersistanceFilter(AppContextPersistanceFilter appContextPersistanceFilter) {
		this.appContextPersistanceFilter = appContextPersistanceFilter;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//@formatter:off
		http
			.csrf().and()
            .addFilter(new WebAsyncManagerIntegrationFilter())
            .headers().and()
            .sessionManagement().and()
            .securityContext().and()
            .anonymous().and()
            .servletApi().and()
		;
		//@formatter:on
	}
}
