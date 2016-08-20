package com.carbonldp.authentication.ticket;

import com.carbonldp.Consts;
import com.carbonldp.authentication.IRIAuthenticationToken;
import com.carbonldp.authentication.token.JWTUtil;
import com.carbonldp.utils.RequestUtil;
import io.jsonwebtoken.*;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static com.carbonldp.Consts.*;

/**
 * @author NestorVenegas
 * @since 0.36.0
 */
public class JWTicketAuthenticationFilter extends GenericFilterBean implements Filter {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	protected final Marker FATAL = MarkerFactory.getMarker( Consts.FATAL );

	private AuthenticationManager authenticationManager;
	private AuthenticationEntryPoint authenticationEntryPoint;

	public JWTicketAuthenticationFilter( AuthenticationManager authenticationManager, AuthenticationEntryPoint authenticationEntryPoint ) {
		this.authenticationManager = authenticationManager;
		this.authenticationEntryPoint = authenticationEntryPoint;
	}

	@Override
	public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain ) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		String jwt = null;
		Map<String, String[]> urlParameters = request.getParameterMap();
		String[] ticketArray = urlParameters.get( TICKET );
		if ( ticketArray != null ) jwt = ticketArray[0];

		if ( jwt == null ) {
			chain.doFilter( request, response );
			return;
		}

		Authentication authResult = null;
		try {
			authResult = authenticate( jwt, httpRequest );
		} catch ( AuthenticationException e ) {
			SecurityContextHolder.clearContext();

			if ( LOG.isDebugEnabled() ) LOG.debug( "Authentication request for failed: " + e );

			authenticationEntryPoint.commence( httpRequest, httpResponse, e );
			return;
		}

		if ( LOG.isDebugEnabled() ) LOG.debug( "Authentication successful: " + authResult );

		SecurityContextHolder.getContext().setAuthentication( authResult );

		chain.doFilter( request, response );
	}

	private Authentication authenticate( String jwt, HttpServletRequest httpRequest ) {
		Map claims = extractAndDecodeHeader( jwt, httpRequest );
		IRI agentIRI = SimpleValueFactory.getInstance().createIRI( (String) claims.get( "sub" ) );
		IRI appRelatedIRI = claims.containsKey( "appRelated" ) ?
			SimpleValueFactory.getInstance().createIRI( (String) claims.get( "appRelated" ) ) :
			null;

		if ( LOG.isDebugEnabled() ) LOG.debug( "JWTicket Authentication Authorization header found for user '" + agentIRI.stringValue() + "'" );

		IRIAuthenticationToken authRequest = new IRIAuthenticationToken( agentIRI, appRelatedIRI );

		return authenticationManager.authenticate( authRequest );
	}

	private Map<String, Object> extractAndDecodeHeader( String jwt, HttpServletRequest httpRequest ) {
		IRI targetIRI = getTargetIRI( httpRequest );
		Map claims = JWTUtil.decode( jwt, targetIRI );
		validateTargetIRI( claims, targetIRI );
		return claims;

	}

	private IRI getTargetIRI( HttpServletRequest request ) {
		String url = RequestUtil.getRequestURL( request );
		return SimpleValueFactory.getInstance().createIRI( url );
	}

	private static void validateTargetIRI( Map<String, Object> claims, IRI targetIRI ) {
		Map targetMap = (Map)claims.get("targetIRI");
		if ( targetMap  == null ) throw new BadCredentialsException( "invalid ticket" );
		String tokenTargetIRI = (String) ((Map)claims.get("targetIRI")).get( "namespace" );
		if ( tokenTargetIRI != null && targetIRI == null ) throw new BadCredentialsException( "invalid target IRI" );

		if ( targetIRI != null ) {
			if ( tokenTargetIRI == null ) throw new BadCredentialsException( "invalid target IRI" );
			if ( ! tokenTargetIRI.equals( targetIRI.stringValue() ) ) throw new BadCredentialsException( "invalid target IRI" );
		}
	}
}
