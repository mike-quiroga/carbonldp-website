package com.base22.carbon.authorization;

import java.util.HashSet;
import java.util.UUID;

import com.base22.carbon.CarbonException;

public interface PrivilegeDAO {
	public HashSet<Privilege> getPrivilegesOfAgent(UUID agentUUID) throws CarbonException;
}
