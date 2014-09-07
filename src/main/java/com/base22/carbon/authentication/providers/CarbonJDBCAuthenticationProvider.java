package com.base22.carbon.authentication.providers;

import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.core.GrantedAuthority;

import com.base22.carbon.CarbonException;
import com.base22.carbon.ConfigurationService;
import com.base22.carbon.agents.Agent;
import com.base22.carbon.agents.AgentDAO;
import com.base22.carbon.apps.roles.ApplicationRole;
import com.base22.carbon.apps.roles.ApplicationRoleDAO;
import com.base22.carbon.authentication.AgentAuthenticationToken;
import com.base22.carbon.authorization.Privilege;
import com.base22.carbon.authorization.PrivilegeDAO;
import com.base22.carbon.groups.Group;
import com.base22.carbon.groups.GroupDAO;
import com.base22.carbon.repository.services.LDPService;

public abstract class CarbonJDBCAuthenticationProvider {

	@Autowired
	protected DriverManagerDataSource jdbcDataSource;
	@Autowired
	protected AgentDAO agentLoginDetailsDAO;
	@Autowired
	protected PrivilegeDAO privilegeDAO;
	@Autowired
	protected GroupDAO groupDAO;
	@Autowired
	protected ApplicationRoleDAO applicationRoleDAO;

	@Autowired
	protected ConfigurationService configurationService;
	@Autowired
	protected LDPService ldpService;

	protected final Logger LOG;

	public CarbonJDBCAuthenticationProvider() {
		this.LOG = LoggerFactory.getLogger(this.getClass());
	}

	protected AgentAuthenticationToken createAgentAuthenticationToken(Agent agent) {
		AgentAuthenticationToken token = null;
		HashSet<Privilege> privileges = null;
		HashSet<Group> groups = null;
		HashSet<ApplicationRole> applicationRoles = new HashSet<ApplicationRole>();
		HashSet<GrantedAuthority> grantedAuthorities = new HashSet<GrantedAuthority>();

		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> createAgentAuthenticationToken() for agent {}", agent.getUuidString());
		}

		// Load the privileges of the agent
		try {
			privileges = privilegeDAO.getPrivilegesOfAgent(agent.getUuid());
			agent.setPrivileges(privileges);
			grantedAuthorities.addAll(privileges);
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- createAgentAuthenticationToken() -- Privileges of the agent loaded: {}.", privileges.size());
			}
		} catch (CarbonException exception) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- createAgentAuthenticationToken() -- The privileges of the agent couln't be loaded.");
			}
		}

		// Load the groups the agent is a member of
		try {
			groups = groupDAO.getAllGroupsOfAgent(agent.getUuid());
			agent.setGroups(groups);
			grantedAuthorities.addAll(groups);
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- createAgentAuthenticationToken() -- Groups of the agent loaded: {}.", groups.size());
			}

			if ( groups.size() != 0 ) {
				// Load the application roles of the groups in which the agent is
				List<ApplicationRole> groupsApplicationRoles = applicationRoleDAO.getApplicationRolesOfGroups(groups);
				applicationRoles.addAll(groupsApplicationRoles);
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("-- createAgentAuthenticationToken() -- ApplicationRoles related to the groups of the agent loaded: {}.", applicationRoles.size());
				}
			}
		} catch (CarbonException exception) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- createAgentAuthenticationToken() -- The groups of the agent couldn't be loaded.");
			}
		}

		// Load the application roles of the agent
		try {
			List<ApplicationRole> agentApplicationRoles = applicationRoleDAO.getApplicationRolesOfAgent(agent.getUuid());
			applicationRoles.addAll(agentApplicationRoles);
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- createAgentAuthenticationToken() -- ApplicationRoles of the agent loaded: {}.", applicationRoles.size());
			}
		} catch (CarbonException exception) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("-- createAgentAuthenticationToken() -- The ApplicationRoles of the agent couldn't be loaded.");
			}
		}

		// Add the application roles to the granted authorities
		agent.setApplicationRoles(applicationRoles);
		grantedAuthorities.addAll(applicationRoles);

		// Load the globalDescription of the agent
		//@formatter:off
		/*
		String globalRepresentationURI = agent.getGlobalRepresentationURI();
		try {
			LDPRSource globalRepresentation = ldpService.getLDPRSource(globalRepresentationURI, configurationService.getPlatformDatasetName());
			agent.setGlobalDescription(globalRepresentation);
			if ( globalRepresentation == null ) {
				// TODO: Do we let the authentication procedure continue? Or do we create a dummy globalRepresentation?
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("-- createAgentAuthenticationToken() -- A globalRepresentation couldn't be found for the agent.");
				}
			}
		} catch (CarbonException exception) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("The globalRepresentation of the agent: {}, couldn't be loaded.", agent.getUuidString());
			}
		}
		*/
		//@formatter:on

		// Create the token
		token = new AgentAuthenticationToken(agent, grantedAuthorities);
		token.eraseCredentials();

		if ( LOG.isTraceEnabled() ) {
			LOG.trace("<< createAgentAuthenticationToken() returning agent token.");
		}

		return token;

	}

	public void setJdbcDataSource(DriverManagerDataSource jdbcDataSource) {
		this.jdbcDataSource = jdbcDataSource;
	}

	public void setAgentDetailsDAO(AgentDAO agentDetailsDAO) {
		this.agentLoginDetailsDAO = agentDetailsDAO;
	}

	public void setPrivilegeDAO(PrivilegeDAO privilegeDAO) {
		this.privilegeDAO = privilegeDAO;
	}

	public void setGroupDAO(GroupDAO groupDAO) {
		this.groupDAO = groupDAO;
	}

	public void setApplicationRoleDAO(ApplicationRoleDAO applicationRoleDAO) {
		this.applicationRoleDAO = applicationRoleDAO;
	}

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

	public void setLdpService(LDPService ldpService) {
		this.ldpService = ldpService;
	}
}
