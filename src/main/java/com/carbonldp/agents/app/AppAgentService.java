package com.carbonldp.agents.app;

import com.carbonldp.agents.Agent;
import com.carbonldp.apps.App;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface AppAgentService {

	// TODO: add security
	public void register( App app, Agent agent );
}
