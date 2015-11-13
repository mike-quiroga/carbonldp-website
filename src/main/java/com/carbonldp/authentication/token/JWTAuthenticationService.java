package com.carbonldp.authentication.token;

import com.carbonldp.AbstractComponent;
import com.carbonldp.Vars;
import com.carbonldp.authentication.AgentAuthenticationToken;
import com.carbonldp.authentication.Token;
import com.carbonldp.authentication.TokenFactory;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

/**
 * @author NestorVenegas
 * @since 0.15.0_ALPHA
 */
public class JWTAuthenticationService extends AbstractComponent implements TokenService {

	public Token createToken() {
		Date expTime = new Date( System.currentTimeMillis() + Vars.getInstance().getTokenExpirationTime() );
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if ( ! ( authentication instanceof AgentAuthenticationToken ) ) throw new RuntimeException( "authentication is not an instance of AgentAuthenticationToken" );
		AgentAuthenticationToken agentToken = (AgentAuthenticationToken) authentication;
		String agentTokenString = agentToken.getAgent().getSubject().stringValue();

		return TokenFactory.getInstance().create( agentTokenString, expTime, signatureAlgorithm );

	}

}
