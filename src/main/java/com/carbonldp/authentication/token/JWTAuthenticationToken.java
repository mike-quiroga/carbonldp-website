package com.carbonldp.authentication.token;

import org.openrdf.model.URI;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collection;

/**
 * @author NestorVenegas
 * @since _version_
 * <p>
 * This class holds the identifier of the identified Agent that gave it's credentials via Token
 * http://jwt.io/introduction/
 */

public class JWTAuthenticationToken extends AbstractAuthenticationToken {
	private URI agentURI;

	public JWTAuthenticationToken( URI agentURI ) {
		super( (Collection) null );
		this.agentURI = agentURI;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return agentURI;
	}

	public URI getAgentURI() {
		return agentURI;
	}
}
