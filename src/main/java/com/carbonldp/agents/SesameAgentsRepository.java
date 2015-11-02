package com.carbonldp.agents;

import org.openrdf.model.URI;

/**
 * @author Dev
 * @since _version_
 */
public class SesameAgentsRepository implements AgentRepository {
	@Override
	public boolean exists( URI agentURI ) {
		return false;
	}

	@Override
	public boolean existsWithEmail( String email ) {
		return false;
	}

	@Override
	public Agent get( URI uri ) {
		return null;
	}

	@Override
	public Agent findByEmail( String email ) {
		return null;
	}

	@Override
	public void create( Agent agent ) {

	}
}
