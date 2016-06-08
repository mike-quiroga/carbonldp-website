package com.carbonldp.authentication.ticket;

import com.carbonldp.Consts;
import com.carbonldp.Vars;
import com.carbonldp.authentication.IRIAuthenticationToken;
import com.carbonldp.exceptions.StupidityException;
import com.carbonldp.utils.RequestUtil;
import io.jsonwebtoken.*;
import org.openrdf.model.IRI;
import org.openrdf.model.impl.SimpleValueFactory;
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
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Map;

import static com.carbonldp.Consts.TICKET;

/**
 * @author NestorVenegas
 * @since _version_
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
		String agentString = extractAndDecodeHeader( jwt, httpRequest );
		IRI agentIRI = SimpleValueFactory.getInstance().createIRI( agentString );

		if ( LOG.isDebugEnabled() ) LOG.debug( "JWTicket Authentication Authorization header found for user '" + agentString + "'" );

		IRIAuthenticationToken authRequest = new IRIAuthenticationToken( agentIRI );

		return authenticationManager.authenticate( authRequest );
	}

	private String extractAndDecodeHeader( String jwt, HttpServletRequest httpRequest ) {
		IRI targetIRI = getTargetIRI( httpRequest );
		byte[] signingKey;
		try {
			signingKey = DatatypeConverter.parseBase64Binary( Vars.getInstance().getTokenKey() );
		} catch ( IllegalArgumentException e ) {
			throw new StupidityException( e );
		}

		try {
			Claims claims = Jwts
				.parser()
				.setSigningKey( signingKey )
				.parseClaimsJws( jwt )
				.getBody();
			validateTargetIRI( claims, targetIRI );
			return claims.getSubject();
		} catch ( UnsupportedJwtException | MalformedJwtException | SignatureException | ExpiredJwtException | IllegalArgumentException e ) {
			throw new BadCredentialsException( "The JSON Web Token isn't valid, nested exception: ", e );
		}
	}

	private void validateTargetIRI( Claims claims, IRI targetIRI ) {
		Map targetIRIClaims = (Map) claims.get( "targetIRI" );
		if ( targetIRIClaims == null ) throw new BadCredentialsException( "invalid target IRI" );

		String tokenTargetIRI = (String) targetIRIClaims.get( "namespace" );
		if ( ! tokenTargetIRI.equals( targetIRI.stringValue() ) ) throw new BadCredentialsException( "invalid target IRI" );
	}

	private IRI getTargetIRI( HttpServletRequest request ) {
		String url = RequestUtil.getRequestURL( request );
		return SimpleValueFactory.getInstance().createIRI( url );
	}

}
