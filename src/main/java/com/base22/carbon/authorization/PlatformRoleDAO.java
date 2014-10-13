package com.base22.carbon.authorization;

import com.base22.carbon.CarbonException;
import com.base22.carbon.agents.Agent;

public interface PlatformRoleDAO {

	public void createRole(PlatformRole role) throws CarbonException;

	public PlatformRole findByName(String name) throws CarbonException;

	public PlatformRole findByID(long id) throws CarbonException;

	// TODO: Edit Methods
	// TODO: Delete Methods

	public void addAgentToRole(Agent agent, PlatformRole role) throws CarbonException;

	public void removeAgentFromRole(Agent agent, PlatformRole role) throws CarbonException;

}
