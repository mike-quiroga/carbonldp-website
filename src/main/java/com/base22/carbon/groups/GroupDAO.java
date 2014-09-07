package com.base22.carbon.groups;

import java.util.HashSet;
import java.util.UUID;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import com.base22.carbon.CarbonException;

public interface GroupDAO {
	@PreAuthorize("hasAuthority('PRIV_CREATE_GROUPS')")
	public void createGroup(Group group) throws CarbonException;

	@PreAuthorize("hasPermission(#parentGroup, 'CREATE_CHILDREN')")
	public void createChildGroup(Group parentGroup, Group childGroup) throws CarbonException;

	@PostAuthorize("hasPermission(returnObject, 'READ')")
	public Group findByUUID(UUID groupUUID) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ')")
	public HashSet<Group> getGroups() throws CarbonException;

	// TODO: Edit methods
	// TODO: Delete methods

	// --- Agent Relations

	@PreAuthorize("hasPermission(#group, 'ADD_AGENTS')")
	public void addAgentToGroup(Group group, UUID agentUUID) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ')")
	public HashSet<Group> getImmediateGroupsOfAgent(UUID agentUUID) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ')")
	public HashSet<Group> getAllGroupsOfAgent(UUID agentUUID) throws CarbonException;

	@PreAuthorize("hasPermission(#group, 'REMOVE_AGENTS')")
	public void removeAgentFromGroup(Group group, UUID agentUUID) throws CarbonException;

	// --- End: Agent Relations
}
