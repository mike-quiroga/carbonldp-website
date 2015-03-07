package com.carbonldp.agents;

import org.openrdf.model.URI;

public interface AgentService {
	public Agent findByEmail( String email );

	public Agent findByURI( URI uri );
}
