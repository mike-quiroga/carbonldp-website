package com.carbonldp.authentication.ticket;

import com.carbonldp.authentication.Ticket;
import org.openrdf.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface TicketService {

	@PreAuthorize( "isAuthenticated() and !( hasRole('ROLE_ANONYMOUS') )" )
	public Ticket createTicket(IRI targetIRI);
}
