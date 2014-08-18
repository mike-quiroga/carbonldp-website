package com.base22.carbon.security.dao;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.security.models.Agent;
import com.base22.carbon.security.models.PlatformRole;

public interface PlatformRoleDAO {

	public void createRole(PlatformRole role) throws CarbonException;

	public PlatformRole findByName(String name) throws CarbonException;

	public PlatformRole findByID(long id) throws CarbonException;

	// TODO: Edit Methods
	// TODO: Delete Methods

	public void addAgentToRole(Agent agent, PlatformRole role) throws CarbonException;

	public void removeAgentFromRole(Agent agent, PlatformRole role) throws CarbonException;

}
