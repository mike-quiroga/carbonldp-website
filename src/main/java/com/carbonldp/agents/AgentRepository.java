package com.carbonldp.agents;

import org.openrdf.model.URI;

public interface AgentRepository {
	public boolean exists( URI agentURI );

	public boolean existsWithEmail( String email );

	public Agent get( URI uri );

	public Agent findByEmail( String email );

	public void create( Agent agent );
}
