package com.base22.carbon.apps.roles;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import com.base22.carbon.CarbonException;
import com.base22.carbon.apps.Application;
import com.base22.carbon.groups.Group;

public interface ApplicationRoleDAO {
	@PreAuthorize("hasAuthority('PRIV_CREATE_APPLICATIONS')")
	public ApplicationRole createRootApplicationRole(Application application, ApplicationRole applicationRole) throws CarbonException;

	@PreAuthorize("hasPermission(#parentRole, 'CREATE_CHILDREN')")
	public ApplicationRole createChildApplicationRole(ApplicationRole parentRole, ApplicationRole childRole) throws CarbonException;

	@PostAuthorize("returnObject == null or hasPermission(returnObject, 'READ')")
	public ApplicationRole findByUUID(UUID applicationRoleUUID) throws CarbonException;

	@PostAuthorize("returnObject == null or hasPermission(returnObject, 'READ')")
	public ApplicationRole findBySlug(String slug, String appSlug) throws CarbonException;

	@PostAuthorize("returnObject == null or hasPermission(returnObject, 'READ')")
	public ApplicationRole findBySlug(String slug, UUID applicationUUID) throws CarbonException;

	@PostAuthorize("returnObject == null or hasPermission(returnObject, 'READ')")
	public ApplicationRole getRootApplicationRoleOfApplication(UUID applicationUUID) throws CarbonException;

	@PostAuthorize("returnObject == null or hasPermission(returnObject, 'READ')")
	public ApplicationRole getApplicationRoleOfApplication(UUID applicationUUID, UUID applicationRoleUUID) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ')")
	public List<ApplicationRole> getApplicationRolesOfApplication(UUID applicationUUID) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ'")
	public List<ApplicationRole> getAllParentsOfApplicationRole(UUID applicationRoleUUID) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ'")
	public List<ApplicationRole> getChildrenOfApplicationRole(UUID applicationRoleUUID) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ'")
	public List<ApplicationRole> getAllChildrenOfApplicationRole(UUID applicationRoleUUID) throws CarbonException;

	// TODO: Edit Methods
	// TODO: Delete Methods

	// --- Agent Related Methods

	@PreAuthorize("hasPermission(#applicationRole, 'ADD_AGENTS')")
	public void addAgentToApplicationRole(ApplicationRole applicationRole, UUID agentUUID) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ')")
	public List<ApplicationRole> getApplicationRolesOfAgent(UUID agentUUID) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ'")
	public List<ApplicationRole> getApplicationRolesOfAgent(UUID agentUUID, UUID applicationUUID) throws CarbonException;

	@PreAuthorize("hasPermission(#applicationRole, 'REMOVE_AGENTS')")
	public void removeAgentFromApplicationRole(ApplicationRole applicationRole, UUID agentUUID) throws CarbonException;

	// --- End: Agent Related Methods
	// --- Group Related Methods

	@PreAuthorize("hasPermission(#applicationRole, 'ADD_GROUP')")
	public void addGroupToApplicationRole(ApplicationRole applicationRole, UUID groupUUID) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ')")
	public List<ApplicationRole> getApplicationRolesOfGroup(UUID groupUUID) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ')")
	public List<ApplicationRole> getApplicationRolesOfGroup(UUID groupUUID, UUID applicationUUID) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ')")
	public List<ApplicationRole> getApplicationRolesOfGroups(Set<Group> groups) throws CarbonException;

	@PostFilter("hasPermission(filterObject, 'READ')")
	public List<ApplicationRole> getApplicationRolesOfGroups(Set<Group> groups, UUID applicationUUID) throws CarbonException;

	@PreAuthorize("hasPermission(#applicationRole, 'REMOVE_GROUP')")
	public void removeGroupFromApplicationRole(ApplicationRole applicationRole, UUID groupUUID) throws CarbonException;

	// --- End: Group Related Methods
}
