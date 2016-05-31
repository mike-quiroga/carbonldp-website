package com.carbonldp.authentication.token;

import com.carbonldp.authentication.Ticket;
import com.carbonldp.authentication.Token;
import org.openrdf.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author NestorVenegas
 * @since 0.15.0-ALPHA
 */
public interface TokenService {
	@PreAuthorize( "isAuthenticated() and !( hasRole('ROLE_ANONYMOUS') )" )
	public Token createToken();

	@PreAuthorize( "isAuthenticated() and !( hasRole('ROLE_ANONYMOUS') )" )
	public Ticket createTicket(IRI targetIRI);
}
