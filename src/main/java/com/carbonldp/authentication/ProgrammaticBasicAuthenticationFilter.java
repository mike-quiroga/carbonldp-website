package com.carbonldp.authentication;

import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class ProgrammaticBasicAuthenticationFilter extends BasicAuthenticationFilter {

	private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new WebAuthenticationDetailsSource();
	private AuthenticationEntryPoint authenticationEntryPoint;
	private AuthenticationManager authenticationManager;
	private RememberMeServices rememberMeServices = new NullRememberMeServices();

	public ProgrammaticBasicAuthenticationFilter( AuthenticationManager authenticationManager ) {
		super( authenticationManager );
	}

	public ProgrammaticBasicAuthenticationFilter( AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint ) {
		super( authenticationManager, authenticationEntryPoint );
		this.authenticationManager = authenticationManager;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	protected void doFilterInternal( HttpServletRequest request,
		HttpServletResponse response, FilterChain chain ) throws IOException,
		ServletException {
		final boolean debug = logger.isDebugEnabled();

		String header = request.getHeader( "Authorization" );

		if ( header == null || ! header.startsWith( "ProgrammaticBasic " ) ) {
			chain.doFilter( request, response );
			return;
		}

		try {
			String[] tokens = extractAndDecodeHeader( header, request );
			assert tokens.length == 2;

			String username = tokens[0];

			if ( debug ) {
				logger.debug( "ProgrammaticBasic Authentication Authorization header found for user '"
					+ username + "'" );
			}

			if ( authenticationIsRequired( username ) ) {
				UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
					username, tokens[1] );
				authRequest.setDetails( authenticationDetailsSource.buildDetails( request ) );
				Authentication authResult = authenticationManager
					.authenticate( authRequest );

				if ( debug ) {
					logger.debug( "Authentication success: " + authResult );
				}

				SecurityContextHolder.getContext().setAuthentication( authResult );

				rememberMeServices.loginSuccess( request, response, authResult );

				onSuccessfulAuthentication( request, response, authResult );
			}

		} catch ( AuthenticationException failed ) {
			SecurityContextHolder.clearContext();

			if ( debug ) {
				logger.debug( "Authentication request for failed: " + failed );
			}

			rememberMeServices.loginFail( request, response );

			onUnsuccessfulAuthentication( request, response, failed );

			authenticationEntryPoint.commence( request, response, failed );

			return;
		}

		chain.doFilter( request, response );
	}

	private String[] extractAndDecodeHeader( String header, HttpServletRequest request )
		throws IOException {

		byte[] base64Token = header.substring( 18 ).getBytes( "UTF-8" );
		byte[] decoded;
		try {
			decoded = Base64.decode( base64Token );
		} catch ( IllegalArgumentException e ) {
			throw new BadCredentialsException(
				"Failed to decode programmatic basic authentication token" );
		}

		String token = new String( decoded, getCredentialsCharset( request ) );

		int delim = token.indexOf( ":" );

		if ( delim == - 1 ) {
			throw new BadCredentialsException( "Invalid programmatic basic authentication token" );
		}
		return new String[]{token.substring( 0, delim ), token.substring( delim + 1 )};
	}

	private boolean authenticationIsRequired( String username ) {
		Authentication existingAuth = SecurityContextHolder.getContext()
														   .getAuthentication();

		if ( existingAuth == null || ! existingAuth.isAuthenticated() ) {
			return true;
		}
		if ( existingAuth instanceof UsernamePasswordAuthenticationToken
			&& ! existingAuth.getName().equals( username ) ) {
			return true;
		}

		if ( existingAuth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken ) {
			return true;
		}

		return false;
	}
}
