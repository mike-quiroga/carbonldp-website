package com.carbonldp.authentication.LDAP;

import com.carbonldp.Consts;
import com.carbonldp.authentication.token.JWTAuthenticationToken;
import org.apache.commons.codec.binary.Base64;
import org.openrdf.model.IRI;
import org.openrdf.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

	public LDAPAuthenticationFilter( AuthenticationManager authenticationManager ) {
		this.authenticationManager = authenticationManager;
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

		authenticate( header );

		chain.doFilter( request, response );
	}

	private Authentication authenticate( String header ) {
		String agentString = Base64.decodeBase64( header.substring( 6 ) ).toString();
		IRI agentIRI = SimpleValueFactory.getInstance().createIRI( agentString );

		if ( LOG.isDebugEnabled() ) LOG.debug( "JWT Authentication Authorization header found for user '" + agentString + "'" );

		JWTAuthenticationToken authRequest = new JWTAuthenticationToken( agentIRI );

		return authenticationManager.authenticate( authRequest );
	}
}