package com.base22.carbon.apps.roles;

import java.util.List;
import java.util.UUID;

import com.base22.carbon.ldp.models.LDPResource;

public interface ApplicationRoleRDF extends LDPResource {
	public UUID getUUID();

	public void setUUID(UUID roleUUID);

	public String getName();

	public void setName(String name);

	public String getDescription();

	public void setDescription(String description);

	public String getSlug();

	public void setSlug(String slug);

	public String getAppSlug();

	public String getAppURI();

	public void setAppURI(String applicationURI);

	public String getParentURI();

	public void setParentURI(String parentURI);

	public List<UUID> getChildRolesUUID();

	public void setChildRolesUUID(List<UUID> childRolesUUID);

	public List<UUID> getAgentsUUID();

	public void setAgentsUUID(List<UUID> agentsUUID);

	public List<UUID> getGroupsUUID();

	public void setGroupsUUID(List<UUID> groupsUUID);

}
