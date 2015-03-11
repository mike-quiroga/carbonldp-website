package com.carbonldp.agents;

import org.openrdf.model.URI;

public interface AgentRepository {
	public Agent findByEmail( String email );

	public Agent findByURI( URI uri );
}
