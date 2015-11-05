package com.carbonldp.agents;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface AgentService {
	@PreAuthorize( "! isAuthenticated() or hasRole('ROLE_ANONYMOUS')" )
	public void register( Agent agent );
}
