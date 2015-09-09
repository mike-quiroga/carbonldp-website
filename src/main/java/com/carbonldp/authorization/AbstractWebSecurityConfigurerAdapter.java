package com.carbonldp.authorization;

import com.carbonldp.apps.context.AppContextPersistenceFilter;
import com.carbonldp.apps.roles.AppRolePersistenceFilter;
import com.carbonldp.authentication.AnonymousAuthenticationFilter;
import com.carbonldp.authentication.AnonymousAuthenticationToken;
import com.carbonldp.authentication.CustomExceptionHandlingConfigurer;
import com.carbonldp.web.cors.CORSAppContextFilter;
import com.carbonldp.web.cors.CORSPlatformContextFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
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
	protected AppRolePersistenceFilter appRolePersistenceFilter;

	@Autowired
	protected CORSAppContextFilter corsAppContextFilter;

	@Autowired
	protected CORSPlatformContextFilter corsPlatformContextFilter;

	@Autowired
	protected AppContextPersistenceFilter appContextPersistenceFilter;

	@Autowired
	protected AnonymousAuthenticationFilter anonymousAuthenticationFilter;

	protected AbstractWebSecurityConfigurerAdapter() {
		super( true );
	}

	@Override
	protected void configure( HttpSecurity http ) throws Exception {
		AuthenticationTrustResolverImpl authenticationTrustResolver = new AuthenticationTrustResolverImpl();
		authenticationTrustResolver.setAnonymousClass( AnonymousAuthenticationToken.class );

		//@formatter:off
		http
			// TODO: Protect against CSRF using another method
			// .csrf().and()
			.addFilter( new WebAsyncManagerIntegrationFilter() )
			.servletApi().and()
			.headers()
				.disable()
			.sessionManagement()
				.sessionCreationPolicy( SessionCreationPolicy.STATELESS )
			.and()
			.securityContext().and()
			.apply( new CustomExceptionHandlingConfigurer<HttpSecurity>() )
				.authenticationEntryPoint( basicAuthenticationEntryPoint )
				.authenticationTrustResolver( authenticationTrustResolver )
			.and()
			.addFilter( basicAuthenticationFilter )
			.addFilterAfter( anonymousAuthenticationFilter, org.springframework.security.web.authentication.AnonymousAuthenticationFilter.class )
		;
		//@formatter:on
	}
}
