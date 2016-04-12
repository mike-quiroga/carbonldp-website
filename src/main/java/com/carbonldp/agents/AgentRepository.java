package com.carbonldp.agents;

import org.openrdf.model.IRI;

public interface AgentRepository {
	public boolean exists( IRI agentIRI );

	public boolean existsWithEmail( String email );

	public Agent get( IRI uri );

	public Agent findByEmail( String email );

	public void create( Agent agent );
}
