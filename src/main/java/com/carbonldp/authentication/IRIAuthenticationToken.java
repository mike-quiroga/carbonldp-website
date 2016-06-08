package com.carbonldp.authentication;

import org.openrdf.model.IRI;
import org.springframework.security.authentication.AbstractAuthenticationToken;

/**
 * This class holds the identifier of the identified Agent that gave it's credentials via Token
 *
 * @author NestorVenegas
 * @see <a href="http://jwt.io/introduction/">JWT Introduction</a>
 * @since 0.15.0-ALPHA
 */

public class IRIAuthenticationToken extends AbstractAuthenticationToken {
	private IRI agentIRI;

	public IRIAuthenticationToken( IRI agentIRI ) {
		super( null );
		this.agentIRI = agentIRI;
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return agentIRI;
	}

	public IRI getAgentIRI() {
		return agentIRI;
	}
}
