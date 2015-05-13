package com.carbonldp.agents.platform;

import com.carbonldp.agents.Agent;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PlatformAgentService {
	@PreAuthorize( "! isAuthenticated() or hasRole('ROLE_ANONYMOUS')" )
	public void register( Agent agent );
}
