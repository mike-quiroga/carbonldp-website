package com.carbonldp.authentication.token;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Vars;
import com.carbonldp.authentication.*;
import com.carbonldp.exceptions.StupidityException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.openrdf.model.IRI;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

/**
 * @author NestorVenegas
 * @since 0.15.0-ALPHA
 */
public class JWTokenAuthenticationService extends AbstractComponent implements TokenService {

	public Token createToken() {
		Date expTime = new Date( System.currentTimeMillis() + Vars.getInstance().getTokenExpirationTime() );
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new StupidityException( "authentication is not an instance of AgentAuthenticationToken" );
		AgentAuthenticationToken agentToken = (AgentAuthenticationToken) authentication;
		String agentTokenString = agentToken.getAgent().getSubject().stringValue();

		String encodedToken = JWTUtil.encode( agentTokenString, expTime );

		return TokenFactory.getInstance().getRDFToken( encodedToken, expTime );

	}

}
