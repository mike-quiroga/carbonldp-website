package com.carbonldp.authentication;

import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.DelegatingAuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import java.util.LinkedHashMap;

public class CustomExceptionHandlingConfigurer<H extends HttpSecurityBuilder<H>> extends CustomAbstractHttpConfigurer<CustomExceptionHandlingConfigurer<H>, H> {
	private AuthenticationEntryPoint authenticationEntryPoint;

	private AccessDeniedHandler accessDeniedHandler;
	private AuthenticationTrustResolver authenticationTrustResolver;

	private LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> defaultEntryPointMappings = new LinkedHashMap<RequestMatcher, AuthenticationEntryPoint>();

	/**
	 * Creates a new instance
	 *
	 * @see HttpSecurity#exceptionHandling()
	 */
	public CustomExceptionHandlingConfigurer() {
	}

	/**
	 * Shortcut to specify the {@link AccessDeniedHandler} to be used is a specific error
	 * page
	 *
	 * @param accessDeniedUrl
	 * 	the URL to the access denied page (i.e. /errors/401)
	 * @return the {@link CustomExceptionHandlingConfigurer} for further customization
	 * @see AccessDeniedHandlerImpl
	 * @see {@link #accessDeniedHandler(org.springframework.security.web.access.AccessDeniedHandler)}
	 */
	public CustomExceptionHandlingConfigurer<H> accessDeniedPage( String accessDeniedUrl ) {
		AccessDeniedHandlerImpl accessDeniedHandler = new AccessDeniedHandlerImpl();
		accessDeniedHandler.setErrorPage( accessDeniedUrl );
		return accessDeniedHandler( accessDeniedHandler );
	}

	/**
	 * Specifies the {@link AccessDeniedHandler} to be used
	 *
	 * @param accessDeniedHandler
	 * 	the {@link AccessDeniedHandler} to be used
	 * @return the {@link CustomExceptionHandlingConfigurer} for further customization
	 */
	public CustomExceptionHandlingConfigurer<H> accessDeniedHandler(
		AccessDeniedHandler accessDeniedHandler ) {
		this.accessDeniedHandler = accessDeniedHandler;
		return this;
	}

	/**
	 * Sets the {@link AuthenticationEntryPoint} to be used.
	 * <p/>
	 * <p>
	 * If no {@link #authenticationEntryPoint(AuthenticationEntryPoint)} is specified,
	 * then
	 * {@link #defaultAuthenticationEntryPointFor(AuthenticationEntryPoint, RequestMatcher)}
	 * will be used. The first {@link AuthenticationEntryPoint} will be used as the
	 * default is no matches were found.
	 * </p>
	 * <p/>
	 * <p>
	 * If that is not provided defaults to {@link Http403ForbiddenEntryPoint}.
	 * </p>
	 *
	 * @param authenticationEntryPoint
	 * 	the {@link AuthenticationEntryPoint} to use
	 * @return the {@link CustomExceptionHandlingConfigurer} for further customizations
	 */
	public CustomExceptionHandlingConfigurer<H> authenticationEntryPoint(
		AuthenticationEntryPoint authenticationEntryPoint ) {
		this.authenticationEntryPoint = authenticationEntryPoint;
		return this;
	}

	public CustomExceptionHandlingConfigurer<H> authenticationTrustResolver( AuthenticationTrustResolver authenticationTrustResolver ) {
		Assert.notNull( authenticationTrustResolver );
		this.authenticationTrustResolver = authenticationTrustResolver;
		return this;
	}

	/**
	 * Sets a default {@link AuthenticationEntryPoint} to be used which prefers being
	 * invoked for the provided {@link RequestMatcher}. If only a single default
	 * {@link AuthenticationEntryPoint} is specified, it will be what is used for the
	 * default {@link AuthenticationEntryPoint}. If multiple default
	 * {@link AuthenticationEntryPoint} instances are configured, then a
	 * {@link DelegatingAuthenticationEntryPoint} will be used.
	 *
	 * @param entryPoint
	 * 	the {@link AuthenticationEntryPoint} to use
	 * @param preferredMatcher
	 * 	the {@link RequestMatcher} for this default
	 * 	{@link AuthenticationEntryPoint}
	 * @return the {@link CustomExceptionHandlingConfigurer} for further customizations
	 */
	public CustomExceptionHandlingConfigurer<H> defaultAuthenticationEntryPointFor(
		AuthenticationEntryPoint entryPoint, RequestMatcher preferredMatcher ) {
		this.defaultEntryPointMappings.put( preferredMatcher, entryPoint );
		return this;
	}

	/**
	 * Gets any explicitly configured {@link AuthenticationEntryPoint}
	 *
	 * @return
	 */
	AuthenticationEntryPoint getAuthenticationEntryPoint() {
		return this.authenticationEntryPoint;
	}

	/**
	 * Gets the {@link AccessDeniedHandler} that is configured.
	 *
	 * @return the {@link AccessDeniedHandler}
	 */
	AccessDeniedHandler getAccessDeniedHandler() {
		return this.accessDeniedHandler;
	}

	@Override
	public void configure( H http ) throws Exception {
		AuthenticationEntryPoint entryPoint = getAuthenticationEntryPoint( http );

		ExceptionTranslationFilter exceptionTranslationFilter = new ExceptionTranslationFilter( entryPoint, getRequestCache( http ) );
		if ( accessDeniedHandler != null ) exceptionTranslationFilter.setAccessDeniedHandler( accessDeniedHandler );
		if ( authenticationTrustResolver != null ) exceptionTranslationFilter.setAuthenticationTrustResolver( authenticationTrustResolver );

		exceptionTranslationFilter = postProcess( exceptionTranslationFilter );
		http.addFilter( exceptionTranslationFilter );
	}

	/**
	 * Gets the {@link AuthenticationEntryPoint} according to the rules specified by
	 * {@link #authenticationEntryPoint(AuthenticationEntryPoint)}
	 *
	 * @param http
	 * 	the {@link HttpSecurity} used to look up shared
	 * 	{@link AuthenticationEntryPoint}
	 * @return the {@link AuthenticationEntryPoint} to use
	 */
	AuthenticationEntryPoint getAuthenticationEntryPoint( H http ) {
		AuthenticationEntryPoint entryPoint = this.authenticationEntryPoint;
		if ( entryPoint == null ) {
			entryPoint = createDefaultEntryPoint( http );
		}
		return entryPoint;
	}

	private AuthenticationEntryPoint createDefaultEntryPoint( H http ) {
		if ( defaultEntryPointMappings.isEmpty() ) {
			return new Http403ForbiddenEntryPoint();
		}
		if ( defaultEntryPointMappings.size() == 1 ) {
			return defaultEntryPointMappings.values().iterator().next();
		}
		DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(
			defaultEntryPointMappings );
		entryPoint.setDefaultEntryPoint( defaultEntryPointMappings.values().iterator()
		                                                          .next() );
		return entryPoint;
	}

	/**
	 * Gets the {@link RequestCache} to use. If one is defined using
	 * {@link #requestCache(org.springframework.security.web.savedrequest.RequestCache)},
	 * then it is used. Otherwise, an attempt to find a {@link RequestCache} shared object
	 * is made. If that fails, an {@link HttpSessionRequestCache} is used
	 *
	 * @param http
	 * 	the {@link HttpSecurity} to attempt to fined the shared object
	 * @return the {@link RequestCache} to use
	 */
	private RequestCache getRequestCache( H http ) {
		RequestCache result = http.getSharedObject( RequestCache.class );
		if ( result != null ) {
			return result;
		}
		return new HttpSessionRequestCache();
	}
}

