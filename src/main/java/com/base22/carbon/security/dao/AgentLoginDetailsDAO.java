package com.base22.carbon.security.dao;

import java.util.UUID;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.security.models.Agent;

public interface AgentLoginDetailsDAO {

	public Agent registerAgentLoginDetails(final Agent agent) throws CarbonException;

	public Agent findByUUID(UUID uuid) throws CarbonException;

	public Agent findByEmail(String email) throws CarbonException;

	public Agent findByKey(String key) throws CarbonException;

	public boolean agentEmailExists(final String email) throws CarbonException;

}
