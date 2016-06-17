package com.carbonldp.authentication.ticket;

import com.carbonldp.authentication.Ticket;
import org.openrdf.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author NestorVenegas
 * @since 0.36.0
 */
public interface TicketService {

	@PreAuthorize( "hasPermission(#targetIRI, 'READ')" )
	public Ticket createTicket(IRI targetIRI);
}