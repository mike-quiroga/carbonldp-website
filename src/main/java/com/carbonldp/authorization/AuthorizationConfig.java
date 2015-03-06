package com.carbonldp.authorization;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;

@Configuration
@EnableWebSecurity
@Order( Ordered.LOWEST_PRECEDENCE )
public class AuthorizationConfig extends AbstractWebSecurityConfigurerAdapter {

	private interface EntryPointOrder {
		public static final int APPS = 1;
		public static final int PLATFORM = 2;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure( http );
		//@formatter:off
		http.antMatcher( "/**" )
			.authorizeRequests()
			.anyRequest().permitAll()
		;
		//@formatter:on
	}

	@Configuration
	@Order( EntryPointOrder.APPS )
	public static class AppsEntryPointConfig extends AbstractWebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			super.configure( http );
			//@formatter:off
			http
					.antMatcher( "/apps/?*/**" )
					.exceptionHandling()
					.authenticationEntryPoint( basicAuthenticationEntryPoint )
					.and()
					.addFilterBefore( appContextPersistanceFilter, SecurityContextPersistenceFilter.class )
					.addFilter( basicAuthenticationFilter )
					.authorizeRequests()
					.anyRequest().authenticated().and()
			;
			//@formatter:on
		}
	}

	@Configuration
	@Order( EntryPointOrder.PLATFORM )
	public static class PlatformEntryPointConfig extends AbstractWebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			super.configure( http );
			//@formatter:off
			http
					.antMatcher( "/platform/**" )
					.exceptionHandling()
					.authenticationEntryPoint( basicAuthenticationEntryPoint )
					.and()
					.addFilter( basicAuthenticationFilter )
					.authorizeRequests()
					.antMatchers( "/platform/apps/" )
					.authenticated()
					.antMatchers( "/platform/apps/?*/" )
					.authenticated()
					.antMatchers( "/platform/roles/" )
					.authenticated()
					.antMatchers( "/platform/roles/?*/" )
					.authenticated()
					.antMatchers( "/platform/permissions/" )
					.authenticated()
					.antMatchers( "/platform/permissions/?*/" )
					.authenticated()
			;
			//@formatter:on
		}
	}
}
