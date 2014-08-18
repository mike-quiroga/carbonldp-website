package com.base22.carbon.security.dao;

import java.util.HashSet;
import java.util.UUID;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.security.models.Privilege;

public interface PrivilegeDAO {
	public HashSet<Privilege> getPrivilegesOfAgent(UUID agentUUID) throws CarbonException;
}
