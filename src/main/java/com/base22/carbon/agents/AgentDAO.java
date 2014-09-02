package com.base22.carbon.agents;

import java.util.List;
import java.util.UUID;

import com.base22.carbon.CarbonException;

public interface AgentDAO {

	public Agent registerAgentLoginDetails(final Agent agent) throws CarbonException;

	public Agent findByUUID(UUID uuid) throws CarbonException;

	public Agent findByEmail(String email) throws CarbonException;

	public Agent findByKey(String key) throws CarbonException;

	public List<Agent> getByEmails(final String[] agentsEmails) throws CarbonException;

	public boolean agentEmailExists(final String email) throws CarbonException;

}
