package com.carbonldp.agents.app;

import com.carbonldp.agents.Agent;
import com.carbonldp.ldp.containers.Container;
import org.openrdf.model.URI;

/**
 * @author NestorVenegas
 * @since 0.10.1-ALPHA
 */
public interface AppAgentRepository {

	public Container createAppRolesContainer( URI rootContainerURI );

	public boolean existsWithEmail( String email );

	public void create( URI appURI, Agent agent );
}
