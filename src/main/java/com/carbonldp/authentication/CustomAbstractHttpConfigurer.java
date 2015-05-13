package com.carbonldp.authentication;

import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.web.DefaultSecurityFilterChain;

public class CustomAbstractHttpConfigurer<T extends CustomAbstractHttpConfigurer<T, B>, B extends HttpSecurityBuilder<B>>
	extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, B> {

	/**
	 * Disables the {@link CustomAbstractHttpConfigurer} by removing it. After doing so a fresh
	 * version of the configuration can be applied.
	 *
	 * @return the {@link HttpSecurityBuilder} for additional customizations
	 */
	@SuppressWarnings( "unchecked" )
	public B disable() {
		getBuilder().removeConfigurer( getClass() );
		return getBuilder();
	}

	@SuppressWarnings( "unchecked" )
	public T withObjectPostProcessor( ObjectPostProcessor<?> objectPostProcessor ) {
		addObjectPostProcessor( objectPostProcessor );
		return (T) this;
	}
}
