package com.carbonldp.agents.app;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.ldp.containers.Container;
import org.openrdf.model.IRI;

/**
 * @author NestorVenegas
 * @since 0.14.0-ALPHA
 */
public interface AppAgentRepository extends AgentRepository {
	public Container createAppAgentsContainer( IRI rootContainerIRI );

}
