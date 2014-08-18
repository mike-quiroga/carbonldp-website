package com.base22.carbon.security.models;

import java.util.List;
import java.util.UUID;

import com.base22.carbon.models.LDPResource;

public interface RDFApplicationRole extends LDPResource {
	public UUID getUUID();

	public void setUUID(UUID roleUUID);

	public String getName();

	public void setName(String name);

	public String getDescription();

	public void setDescription(String description);

	public UUID getApplicationUUID();

	public void setApplicationUUID(UUID applicationUUID);

	public UUID getParentUUID();

	public void setParentUUID(UUID parentUUID);

	public List<UUID> getChildRolesUUID();

	public void setChildRolesUUID(List<UUID> childRolesUUID);

	public List<UUID> getAgentsUUID();

	public void setAgentsUUID(List<UUID> agentsUUID);

	public List<UUID> getGroupsUUID();

	public void setGroupsUUID(List<UUID> groupsUUID);

}
