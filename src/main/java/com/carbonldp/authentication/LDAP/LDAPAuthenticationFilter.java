package com.carbonldp.authentication.LDAP;

import com.carbonldp.Consts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since _version_
 */
public class LDAPAuthenticationFilter extends GenericFilterBean implements Filter {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	protected final Marker FATAL = MarkerFactory.getMarker( Consts.FATAL );

	private AuthenticationManager authenticationManager;
	private AuthenticationEntryPoint authenticationEntryPoint;

	public LDAPAuthenticationFilter( AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint ) {
		this.authenticationManager = authenticationManager;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String header = httpRequest.getHeader( "Authorization" );

		if ( header == null || ! header.startsWith( "Basic " ) || SecurityContextHolder.getContext().getAuthentication() != null ) {
			chain.doFilter( request, response );
			return;
		}

		String[] credentials;
		try {
			credentials = getCredentials( header );
			assert credentials.length == 2;
		} catch ( AuthenticationException e ) {
			SecurityContextHolder.clearContext();

			if ( LOG.isDebugEnabled() ) LOG.debug( "Authentication request for failed: " + e );

			authenticationEntryPoint.commence( httpRequest, httpResponse, e );
			return;
		}




		if ( LOG.isDebugEnabled() ) LOG.debug( "Authentication successful: " + credentials[0] );

		SecurityContextHolder.getContext().setAuthentication( null );

		chain.doFilter( request, response );
	}

	private String[] getCredentials( String header ) {
		String token = header.substring( 6 );
		byte[] decoded = java.util.Base64.getDecoder().decode( token );
		String credentials = new String( decoded );

		int delim = credentials.indexOf( ":" );

		if ( delim == - 1 ) {
			throw new BadCredentialsException( "Invalid basic authentication token" );
		}
		return new String[]{credentials.substring( 0, delim ), credentials.substring( delim + 1 )};
	}
}