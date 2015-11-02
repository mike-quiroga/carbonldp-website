package com.carbonldp.agents.app;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.ldp.containers.Container;
import org.openrdf.model.URI;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface AppAgentRepository extends AgentRepository {
	public Container createAppAgentsContainer( URI rootContainerURI );

}
