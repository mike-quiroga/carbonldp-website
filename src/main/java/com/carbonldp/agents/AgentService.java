package com.carbonldp.agents;

import org.openrdf.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author NestorVenegas
 * @since 0.14.0-ALPHA
 */
public interface AgentService {
	@PreAuthorize( "! isAuthenticated() or hasRole('ROLE_ANONYMOUS')" )
	public void register( Agent agent );

	@PreAuthorize( "hasPermission(#source, 'UPDATE')" )
	public void replace( IRI AgentIRI, Agent agent );
}
