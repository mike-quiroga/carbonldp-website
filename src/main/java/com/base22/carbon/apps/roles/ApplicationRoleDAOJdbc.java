package com.base22.carbon.apps.roles;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Service;

import com.base22.carbon.CarbonException;
import com.base22.carbon.DAOException;
import com.base22.carbon.agents.AgentDAOJdbc;
import com.base22.carbon.apps.Application;
import com.base22.carbon.apps.ApplicationDAOJdbc;
import com.base22.carbon.authentication.AuthenticationUtil;
import com.base22.carbon.groups.Group;
import com.base22.carbon.groups.GroupDAOJdbc;
import com.base22.carbon.jdbc.QueryTransactionCallback;
import com.base22.carbon.jdbc.QueryTransactionTemplate;
import com.base22.carbon.jdbc.TransactionException;
import com.base22.carbon.jdbc.UpdateTransactionCallback;
import com.base22.carbon.jdbc.UpdateTransactionTemplate;
import com.base22.carbon.jdbc.DAOJdbc;
import com.base22.carbon.models.UUIDObject;

@Service("applicationRoleDAO")
public class ApplicationRoleDAOJdbc extends DAOJdbc implements ApplicationRoleDAO {

	public static final String TABLE = "application_roles";
	public static final String UUID_FIELD = "uuid";
	public static final String HEX_UUID_FIELD = "hex_uuid";
	public static final String PARENT_UUID_FIELD = "parent_uuid";
	public static final String PARENT_HEX_UUID_FIELD = "parent_hex_uuid";
	public static final String NAME_FIELD = "name";
	public static final String DESCRIPTION_FIELD = "description";

	public static final String EXTERNAL_ID_FIELD = "application_role_uuid";

	public static final String AGENTS_APPLICATION_ROLES_TABLE = "agents_application_roles";
	public static final String APPLICATION_ROLES_GROUPS_TABLE = "application_roles_groups";

	public ApplicationRole createRootApplicationRole(Application application, ApplicationRole applicationRole) throws CarbonException {
		// Check if the application has already a root application role
		boolean alreadyExists = false;
		alreadyExists = getRootApplicationRoleOfApplication(application.getUuid()) != null;
		if ( alreadyExists ) {
			throw new DAOException("The application already has a root application role.");
		}

		// Check if the UUID was provided
		if ( applicationRole.getUuid() == null ) {
			// It wasn't, generate one
			applicationRole.setUuid(UUID.randomUUID());
		}

		applicationRole.setApplicationUUID(application.getUuid());
		applicationRole.setParentUUID(null);

		// Create the application role
		applicationRole = createApplicationRole(applicationRole);

		return applicationRole;
	}

	public ApplicationRole createChildApplicationRole(ApplicationRole parentRole, ApplicationRole childRole) throws CarbonException {
		// Check if the UUID was provided
		if ( childRole.getUuid() == null ) {
			// It wasn't, generate one
			childRole.setUuid(UUID.randomUUID());
		}

		childRole.setApplicationUUID(parentRole.getApplicationUUID());
		childRole.setParentUUID(parentRole.getUuid());

		childRole = createApplicationRole(childRole);

		return childRole;
	}

	private ApplicationRole createApplicationRole(final ApplicationRole applicationRole) throws CarbonException {
		final String applicationUUIDString = AuthenticationUtil.minimizeUUID(applicationRole.getApplicationUUID());
		final String applicationRoleUUIDString = applicationRole.getMinimizedUuidString();

		String parentUUIDString = null;
		if ( applicationRole.getParentUUID() != null ) {
			parentUUIDString = AuthenticationUtil.minimizeUUID(applicationRole.getParentUUID());
		}
		final String parentUUIDStringToInsert = parentUUIDString;

		int insertionResult;
		UpdateTransactionTemplate template = new UpdateTransactionTemplate();
		try {
			//@formatter:off
			insertionResult = template.execute(securityJDBCDataSource, new UpdateTransactionCallback() {
				//@formatter:on
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder queryBuilder) {
					//@formatter:off
					queryBuilder
						.append("INSERT INTO ")
						.append(TABLE)
						.append(" (")
							.append(UUID_FIELD)
							.append(", ")
							.append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
							.append(", ")
							.append(PARENT_UUID_FIELD)
							.append(", ")
							.append(NAME_FIELD)
							.append(", ")
							.append(DESCRIPTION_FIELD)
						.append(") VALUES (UNHEX(?), UNHEX(?), UNHEX(?), ?, ?)")
					;
					//@formatter:on
					return queryBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, applicationRoleUUIDString);
					statement.setString(2, applicationUUIDString);
					setStringOrNull(statement, 3, parentUUIDStringToInsert);
					statement.setString(4, applicationRole.getName());
					setStringOrNull(statement, 5, applicationRole.getDescription());
					return statement;
				}
			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createApplicationRole() > The applicationRole with UUID: '{}', couldn't be created.", applicationRole.getUuidString());
			}
			throw e;
		}

		if ( insertionResult != 1 ) {
			// TODO: Can we get the reason in this case?
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createApplicationRole() > The applicationRole with UUID: '{}', couldn't be created.", applicationRole.getUuidString());
			}
			throw new DAOException("The applicationRole couldn't be created.");
		}

		return applicationRole;
	}

	public ApplicationRole findByUUID(UUID applicationRoleUUID) throws CarbonException {
		ApplicationRole role = null;

		final String uuidString = AuthenticationUtil.minimizeUUID(applicationRoleUUID);

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			role = template.execute(securityJDBCDataSource, new QueryTransactionCallback<ApplicationRole>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT *, HEX(")
					 			.append(UUID_FIELD)
					 		.append(") AS ").append(HEX_UUID_FIELD)
					 		.append(", HEX(")
					 			.append(PARENT_UUID_FIELD)
					 		.append(") AS ").append(PARENT_HEX_UUID_FIELD)
					 		.append(", HEX(")
					 			.append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
					 		.append(") AS ").append(ApplicationDAOJdbc.EXTERNAL_HEX_ID_FIELD)
						.append(" FROM ")
							.append(TABLE)
						.append(" WHERE ")
							.append(UUID_FIELD)
						.append(" = UNHEX(?)")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, uuidString);
					return statement;
				}

				@Override
				public ApplicationRole interpretResultSet(ResultSet resultSet) throws SQLException {
					List<ApplicationRole> roles = null;

					roles = populateApplicationRoles(resultSet);

					if ( roles.isEmpty() ) {
						return null;
					}

					return roles.get(0);
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< findByUUID() > There was a problem while trying to find the applicationRole with UUID: '{}'.", applicationRoleUUID.toString());
			}
			throw e;
		}

		return role;
	}

	public ApplicationRole getRootApplicationRoleOfApplication(UUID applicationUUID) throws CarbonException {
		// TODO Auto-generated method stub
		return null;
	}

	@PostAuthorize("returnObject == null or hasPermission(returnObject, 'READ')")
	public ApplicationRole getApplicationRoleOfApplication(UUID applicationUUID, UUID applicationRoleUUID) throws CarbonException {
		ApplicationRole role = null;

		final String uuidString = AuthenticationUtil.minimizeUUID(applicationRoleUUID);
		final String appUUIDString = AuthenticationUtil.minimizeUUID(applicationUUID);

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			role = template.execute(securityJDBCDataSource, new QueryTransactionCallback<ApplicationRole>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT *, HEX(")
					 			.append(UUID_FIELD)
					 		.append(") AS ").append(HEX_UUID_FIELD)
					 		.append(", HEX(")
					 			.append(PARENT_UUID_FIELD)
					 		.append(") AS ").append(PARENT_HEX_UUID_FIELD)
					 		.append(", HEX(")
					 			.append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
					 		.append(") AS ").append(ApplicationDAOJdbc.EXTERNAL_HEX_ID_FIELD)
						.append(" FROM ")
							.append(TABLE)
						.append(" WHERE ")
							.append(UUID_FIELD)
						.append(" = UNHEX(?) AND ")
							.append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
						.append(" = UNHEX(?)")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, uuidString);
					statement.setString(2, appUUIDString);
					return statement;
				}

				@Override
				public ApplicationRole interpretResultSet(ResultSet resultSet) throws SQLException {
					List<ApplicationRole> roles = null;

					roles = populateApplicationRoles(resultSet);

					if ( roles.isEmpty() ) {
						return null;
					}

					return roles.get(0);
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getApplicationRoleOfApplication() > There was a problem while trying to find the applicationRole with UUID: '{}'.",
						applicationRoleUUID.toString());
			}
			throw e;
		}

		return role;
	}

	public List<ApplicationRole> getApplicationRolesOfApplication(UUID applicationUUID) throws CarbonException {
		List<ApplicationRole> roles = null;

		final String uuidString = AuthenticationUtil.minimizeUUID(applicationUUID);

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			roles = template.execute(securityJDBCDataSource, new QueryTransactionCallback<List<ApplicationRole>>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT *, HEX(")
					 			.append(UUID_FIELD)
					 		.append(") AS ").append(HEX_UUID_FIELD)
					 		.append(", HEX(")
					 			.append(PARENT_UUID_FIELD)
					 		.append(") AS ").append(PARENT_HEX_UUID_FIELD)
					 		.append(", HEX(")
					 			.append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
					 		.append(") AS ").append(ApplicationDAOJdbc.EXTERNAL_HEX_ID_FIELD)
						.append(" FROM ")
							.append(TABLE)
						.append(" WHERE ")
							.append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
						.append(" = UNHEX(?)")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, uuidString);
					return statement;
				}

				@Override
				public List<ApplicationRole> interpretResultSet(ResultSet resultSet) throws SQLException {
					return populateApplicationRoles(resultSet);
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error(
						"<< getApplicationRolesOfApplication() > There was a problem while trying to get the applicationRoles of the application with UUID: '{}'.",
						applicationUUID.toString());
			}
			throw e;
		}

		return roles;

	}

	public List<ApplicationRole> getAllParentsOfApplicationRole(UUID applicationRoleUUID) throws CarbonException {
		List<ApplicationRole> parentRoles = null;

		final String roleUUIDString = AuthenticationUtil.minimizeUUID(applicationRoleUUID);

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			parentRoles = template.execute(securityJDBCDataSource, new QueryTransactionCallback<List<ApplicationRole>>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder.
					 	append("SELECT ")
					 		.append("HEX(T2.")
					 			.append(UUID_FIELD)
					 		.append(") AS ").append(HEX_UUID_FIELD)
					 		.append(", HEX(T2.")
					 			.append(PARENT_UUID_FIELD)
					 		.append(") AS ").append(PARENT_HEX_UUID_FIELD)
					 		.append(", HEX(T2.")
					 			.append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
					 		.append(") AS ").append(ApplicationDAOJdbc.EXTERNAL_HEX_ID_FIELD)
					 		.append(", T2.").append(NAME_FIELD)
					 		.append(", T2.").append(DESCRIPTION_FIELD)
					 		.append(", T1.lvl ")
					 	.append("FROM ( ")
					 		.append("SELECT ")
					 			.append("@r AS _").append(UUID_FIELD)
					 			.append(", (")
					 				.append("SELECT @r := ").append(PARENT_UUID_FIELD).append(" FROM ").append(TABLE).append(" WHERE ").append(UUID_FIELD).append(" = _").append(UUID_FIELD)
					 			.append(") AS ").append(PARENT_UUID_FIELD)
					 			.append(", @l := @l + 1 AS lvl ")
						    .append("FROM ( ")
						            .append("SELECT @r := UNHEX(?), @l := 0 ")
						    .append(") vars, ").append(TABLE).append(" h ")
						    .append("WHERE @r IS NOT NULL ")
						.append(") T1 ")
						.append("JOIN ").append(TABLE).append(" T2 ")
						.append("ON T1._").append(UUID_FIELD).append(" = T2.").append(UUID_FIELD)
						.append(" ORDER BY T1.lvl DESC")
					 ;
					/*
					
						SELECT 
							HEX(T2.uuid) AS hex_uuid, 
						    HEX(T2.parent_uuid) AS parent_hex_uuid, 
						    HEX(T2.application_uuid) AS application_hex_uuid, 
						    T2.name, 
						    T2.description, 
						    T1.lvl 
						FROM ( 
						    SELECT @r AS _uuid, (
						        SELECT @r := parent_uuid 
						        FROM application_roles 
						        WHERE uuid = _uuid
						    ) AS parent_uuid, @l := @l + 1 AS lvl FROM ( 
						        SELECT @r := UNHEX(?), @l := 0 
						    ) vars, application_roles h WHERE @r IS NOT NULL 
						) T1 JOIN application_roles T2 ON T1._uuid = T2.uuid ORDER BY T1.lvl DESC
					
					 */
					
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, roleUUIDString);
					return statement;
				}

				@Override
				public List<ApplicationRole> interpretResultSet(ResultSet resultSet) throws SQLException {
					List<ApplicationRole> applicationRoles = null;

					applicationRoles = populateParentApplicationRoles(resultSet);

					// Remove the last element (That is actually the child)
					if ( ! applicationRoles.isEmpty() ) {
						applicationRoles.remove(applicationRoles.size() - 1);
					}

					return applicationRoles;
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getAllParentsOfApplicationRole() > There was a problem while trying to find the parents of the applicationRole: '{}'.",
						roleUUIDString);
			}
			throw e;
		}

		return parentRoles;
	}

	public List<ApplicationRole> getChildrenOfApplicationRole(UUID applicationRoleUUID) throws CarbonException {
		List<ApplicationRole> roles = null;

		final String uuidString = AuthenticationUtil.minimizeUUID(applicationRoleUUID);

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			roles = template.execute(securityJDBCDataSource, new QueryTransactionCallback<List<ApplicationRole>>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT *, HEX(")
					 			.append(UUID_FIELD)
					 		.append(") AS ").append(HEX_UUID_FIELD)
					 		.append(", HEX(")
					 			.append(PARENT_UUID_FIELD)
					 		.append(") AS ").append(PARENT_HEX_UUID_FIELD)
					 		.append(", HEX(")
					 			.append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
					 		.append(") AS ").append(ApplicationDAOJdbc.EXTERNAL_HEX_ID_FIELD)
						.append(" FROM ")
							.append(TABLE)
						.append(" WHERE ")
							.append(PARENT_UUID_FIELD)
						.append(" = UNHEX(?)")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, uuidString);
					return statement;
				}

				@Override
				public List<ApplicationRole> interpretResultSet(ResultSet resultSet) throws SQLException {
					List<ApplicationRole> roles = null;

					roles = populateApplicationRoles(resultSet);

					return roles;
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getChildrenOfApplicationRole() > There was a problem while trying to find the children of the applicationRole with UUID: '{}'.",
						applicationRoleUUID.toString());
			}
			throw e;
		}

		return roles;
	}

	public List<ApplicationRole> getAllChildrenOfApplicationRole(UUID applicationRoleUUID) throws CarbonException {
		List<ApplicationRole> children = new ArrayList<ApplicationRole>();
		List<ApplicationRole> firstChildren = null;
		try {
			firstChildren = getChildrenOfApplicationRole(applicationRoleUUID);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}

		children.addAll(firstChildren);

		for (ApplicationRole firstChild : firstChildren) {
			List<ApplicationRole> nodeChildren = null;
			try {
				nodeChildren = getChildrenOfApplicationRole(firstChild.getUuid());
			} catch (CarbonException e) {
				// TODO: FT
				throw e;
			}
			children.addAll(nodeChildren);
		}

		return children;
	}

	public void addAgentToApplicationRole(ApplicationRole applicationRole, UUID agentUUID) throws CarbonException {
		final String applicationRoleUUIDString = applicationRole.getMinimizedUuidString();
		final String agentUUIDString = AuthenticationUtil.minimizeUUID(agentUUID);

		int insertionResult;
		UpdateTransactionTemplate template = new UpdateTransactionTemplate();
		try {
			//@formatter:off
			insertionResult = template.execute(securityJDBCDataSource, new UpdateTransactionCallback() {
				//@formatter:on
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder queryBuilder) {
					//@formatter:off
					queryBuilder
						.append("INSERT INTO ").append(AGENTS_APPLICATION_ROLES_TABLE)
							.append("(").append(AgentDAOJdbc.EXTERNAL_ID_FIELD).append(", ").append(EXTERNAL_ID_FIELD).append(")")
						.append("VALUES (UNHEX(?), UNHEX(?))")
					;
					//@formatter:on
					return queryBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, agentUUIDString);
					statement.setString(2, applicationRoleUUIDString);
					return statement;
				}
			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< addAgentToApplicationRole() > The agent: '{}', couldn't be added to the applicationRole: '{}'.", agentUUID.toString(),
						applicationRole.getUuidString());
			}
			throw e;
		}

		if ( insertionResult != 1 ) {
			// TODO: Can we get the reason in this case?
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< addAgentToApplicationRole() > The agent: '{}', couldn't be added to the applicationRole: '{}'.", agentUUID.toString(),
						applicationRole.getUuidString());
			}
			throw new DAOException("The agent couldn't be added to the applicationRole.");
		}

		// TODO Auto-generated method stub
	}

	public List<ApplicationRole> getApplicationRolesOfAgent(UUID agentUUID) throws CarbonException {
		List<ApplicationRole> applicationRoles = new ArrayList<ApplicationRole>();

		final String agentUUIDString = AuthenticationUtil.minimizeUUID(agentUUID);

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			applicationRoles = template.execute(securityJDBCDataSource, new QueryTransactionCallback<List<ApplicationRole>>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT ")
							.append(TABLE).append(".*, HEX(")
								.append(TABLE).append(".").append(UUID_FIELD)
							.append(") AS ").append(HEX_UUID_FIELD)
							.append(", HEX(")
								.append(TABLE).append(".").append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
							.append(") AS ").append(ApplicationDAOJdbc.EXTERNAL_HEX_ID_FIELD)
							.append(", HEX(")
								.append(TABLE).append(".").append(PARENT_UUID_FIELD)
							.append(") AS ").append(PARENT_HEX_UUID_FIELD)
						.append(" FROM ").append(AGENTS_APPLICATION_ROLES_TABLE)
						.append(" INNER JOIN ").append(TABLE)
						.append(" ON ").append(AGENTS_APPLICATION_ROLES_TABLE).append(".").append(EXTERNAL_ID_FIELD).append(" = ").append(TABLE).append(".").append(UUID_FIELD)
						.append(" WHERE ").append(AGENTS_APPLICATION_ROLES_TABLE).append(".").append(AgentDAOJdbc.EXTERNAL_ID_FIELD).append(" = UNHEX(?)")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, agentUUIDString);
					return statement;
				}

				@Override
				public List<ApplicationRole> interpretResultSet(ResultSet resultSet) throws SQLException {
					List<ApplicationRole> applicationRoles = null;

					applicationRoles = populateApplicationRoles(resultSet);

					return applicationRoles;
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getApplicationRolesOfAgent() > There was a problem while trying to find the applicationRoles of the agent: '{}'.",
						agentUUID.toString());
			}
			throw e;
		}

		return applicationRoles;
	}

	public List<ApplicationRole> getApplicationRolesOfAgent(UUID agentUUID, UUID applicationUUID) throws CarbonException {
		List<ApplicationRole> applicationRoles = new ArrayList<ApplicationRole>();

		final String agentUUIDString = AuthenticationUtil.minimizeUUID(agentUUID);
		final String applicationUUIDString = AuthenticationUtil.minimizeUUID(applicationUUID);

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			applicationRoles = template.execute(securityJDBCDataSource, new QueryTransactionCallback<List<ApplicationRole>>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT ")
							.append(TABLE).append(".*, HEX(")
								.append(TABLE).append(".").append(UUID_FIELD)
							.append(") AS ").append(HEX_UUID_FIELD)
							.append(", HEX(")
								.append(TABLE).append(".").append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
							.append(") AS ").append(ApplicationDAOJdbc.EXTERNAL_HEX_ID_FIELD)
							.append(", HEX(")
								.append(TABLE).append(".").append(PARENT_UUID_FIELD)
							.append(") AS ").append(PARENT_HEX_UUID_FIELD)
						.append(" FROM ").append(AGENTS_APPLICATION_ROLES_TABLE)
						.append(" INNER JOIN ").append(TABLE)
						.append(" ON ").append(AGENTS_APPLICATION_ROLES_TABLE).append(".").append(EXTERNAL_ID_FIELD).append(" = ").append(TABLE).append(".").append(UUID_FIELD)
						.append(" WHERE ").append(AGENTS_APPLICATION_ROLES_TABLE).append(".").append(AgentDAOJdbc.EXTERNAL_ID_FIELD).append(" = UNHEX(?)")
						.append(" AND ").append(TABLE).append(".").append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD).append(" = UNHEX(?)")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, agentUUIDString);
					statement.setString(2, applicationUUIDString);
					return statement;
				}

				@Override
				public List<ApplicationRole> interpretResultSet(ResultSet resultSet) throws SQLException {
					List<ApplicationRole> applicationRoles = null;

					applicationRoles = populateApplicationRoles(resultSet);

					return applicationRoles;
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error(
						"<< getApplicationRolesOfAgent() > There was a problem while trying to find the applicationRoles of the agent: '{}' in the application: '{}'.",
						agentUUID.toString(), applicationUUID.toString());
			}
			throw e;
		}

		return applicationRoles;
	}

	public void removeAgentFromApplicationRole(ApplicationRole applicationRole, UUID agentUUID) throws CarbonException {
		// TODO Auto-generated method stub

	}

	public void addGroupToApplicationRole(ApplicationRole applicationRole, UUID groupUUID) throws CarbonException {
		// TODO Auto-generated method stub

	}

	public List<ApplicationRole> getApplicationRolesOfGroup(UUID groupUUID) throws CarbonException {
		List<ApplicationRole> applicationRoles = new ArrayList<ApplicationRole>();

		final String groupUUIDString = AuthenticationUtil.minimizeUUID(groupUUID);

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			applicationRoles = template.execute(securityJDBCDataSource, new QueryTransactionCallback<List<ApplicationRole>>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT ")
							.append(TABLE).append(".*, HEX(")
								.append(TABLE).append(".").append(UUID_FIELD)
							.append(") AS ").append(HEX_UUID_FIELD)
							.append(", HEX(")
								.append(TABLE).append(".").append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
							.append(") AS ").append(ApplicationDAOJdbc.EXTERNAL_HEX_ID_FIELD)
							.append(", HEX(")
								.append(TABLE).append(".").append(PARENT_UUID_FIELD)
							.append(") AS ").append(PARENT_HEX_UUID_FIELD)
						.append(" FROM ").append(APPLICATION_ROLES_GROUPS_TABLE)
						.append(" INNER JOIN ").append(TABLE)
						.append(" ON ").append(APPLICATION_ROLES_GROUPS_TABLE).append(".").append(EXTERNAL_ID_FIELD).append(" = ").append(TABLE).append(".").append(UUID_FIELD)
						.append(" WHERE ").append(APPLICATION_ROLES_GROUPS_TABLE).append(".").append(GroupDAOJdbc.EXTERNAL_ID_FIELD).append(" = UNHEX(?)")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, groupUUIDString);
					return statement;
				}

				@Override
				public List<ApplicationRole> interpretResultSet(ResultSet resultSet) throws SQLException {
					List<ApplicationRole> applicationRoles = null;

					applicationRoles = populateApplicationRoles(resultSet);

					return applicationRoles;
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getApplicationRolesOfGroup() > There was a problem while trying to find the applicationRoles of the group: '{}'.",
						groupUUID.toString());
			}
			throw e;
		}

		return applicationRoles;
	}

	public List<ApplicationRole> getApplicationRolesOfGroup(UUID groupUUID, UUID applicationUUID) throws CarbonException {
		List<ApplicationRole> applicationRoles = new ArrayList<ApplicationRole>();

		final String groupUUIDString = AuthenticationUtil.minimizeUUID(groupUUID);
		final String applicationUUIDString = AuthenticationUtil.minimizeUUID(applicationUUID);

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			applicationRoles = template.execute(securityJDBCDataSource, new QueryTransactionCallback<List<ApplicationRole>>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT ")
							.append(TABLE).append(".*, HEX(")
								.append(TABLE).append(".").append(UUID_FIELD)
							.append(") AS ").append(HEX_UUID_FIELD)
							.append(", HEX(")
								.append(TABLE).append(".").append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
							.append(") AS ").append(ApplicationDAOJdbc.EXTERNAL_HEX_ID_FIELD)
							.append(", HEX(")
								.append(TABLE).append(".").append(PARENT_UUID_FIELD)
							.append(") AS ").append(PARENT_HEX_UUID_FIELD)
						.append(" FROM ").append(APPLICATION_ROLES_GROUPS_TABLE)
						.append(" INNER JOIN ").append(TABLE)
						.append(" ON ").append(APPLICATION_ROLES_GROUPS_TABLE).append(".").append(EXTERNAL_ID_FIELD).append(" = ").append(TABLE).append(".").append(UUID_FIELD)
						.append(" WHERE ").append(APPLICATION_ROLES_GROUPS_TABLE).append(".").append(GroupDAOJdbc.EXTERNAL_ID_FIELD).append(" = UNHEX(?)")
						.append(" AND ").append(TABLE).append(".").append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD).append(" = UNHEX(?)")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, groupUUIDString);
					statement.setString(2, applicationUUIDString);
					return statement;
				}

				@Override
				public List<ApplicationRole> interpretResultSet(ResultSet resultSet) throws SQLException {
					List<ApplicationRole> applicationRoles = null;

					applicationRoles = populateApplicationRoles(resultSet);

					return applicationRoles;
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error(
						"<< getApplicationRolesOfGroup() > There was a problem while trying to find the applicationRoles of the group: '{}', in the application: '{}'.",
						groupUUID.toString(), applicationUUID.toString());
			}
			throw e;
		}

		return applicationRoles;
	}

	public List<ApplicationRole> getApplicationRolesOfGroups(Set<Group> groups) throws CarbonException {
		List<ApplicationRole> applicationRoles = new ArrayList<ApplicationRole>();

		final String[] groupMinimizedUUIDs = UUIDObject.getMinimizedUUIDStrings(groups);

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			applicationRoles = template.execute(securityJDBCDataSource, new QueryTransactionCallback<List<ApplicationRole>>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT ")
							.append(TABLE).append(".*, HEX(")
								.append(TABLE).append(".").append(UUID_FIELD)
							.append(") AS ").append(HEX_UUID_FIELD)
							.append(", HEX(")
								.append(TABLE).append(".").append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
							.append(") AS ").append(ApplicationDAOJdbc.EXTERNAL_HEX_ID_FIELD)
							.append(", HEX(")
								.append(TABLE).append(".").append(PARENT_UUID_FIELD)
							.append(") AS ").append(PARENT_HEX_UUID_FIELD)
						.append(" FROM ").append(APPLICATION_ROLES_GROUPS_TABLE)
						.append(" INNER JOIN ").append(TABLE)
						.append(" ON ").append(APPLICATION_ROLES_GROUPS_TABLE).append(".").append(EXTERNAL_ID_FIELD).append(" = ").append(TABLE).append(".").append(UUID_FIELD)
						.append(" WHERE ").append(APPLICATION_ROLES_GROUPS_TABLE).append(".").append(GroupDAOJdbc.EXTERNAL_ID_FIELD).append(" IN (").append(prepareUUIDPlaceHolders(groupMinimizedUUIDs.length)).append(")")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					setStringsInStatement(statement, groupMinimizedUUIDs);
					return statement;
				}

				@Override
				public List<ApplicationRole> interpretResultSet(ResultSet resultSet) throws SQLException {
					List<ApplicationRole> applicationRoles = null;

					applicationRoles = populateApplicationRoles(resultSet);

					return applicationRoles;
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getApplicationRolesOfGroups() > There was a problem while trying to find the applicationRoles of the groups.");
			}
			throw e;
		}

		return applicationRoles;
	}

	public List<ApplicationRole> getApplicationRolesOfGroups(Set<Group> groups, UUID applicationUUID) throws CarbonException {
		List<ApplicationRole> applicationRoles = new ArrayList<ApplicationRole>();

		final String[] groupMinimizedUUIDs = UUIDObject.getMinimizedUUIDStrings(groups);
		final String applicationUUIDString = AuthenticationUtil.minimizeUUID(applicationUUID);

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			applicationRoles = template.execute(securityJDBCDataSource, new QueryTransactionCallback<List<ApplicationRole>>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT ")
							.append(TABLE).append(".*, HEX(")
								.append(TABLE).append(".").append(UUID_FIELD)
							.append(") AS ").append(HEX_UUID_FIELD)
							.append(", HEX(")
								.append(TABLE).append(".").append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD)
							.append(") AS ").append(ApplicationDAOJdbc.EXTERNAL_HEX_ID_FIELD)
							.append(", HEX(")
								.append(TABLE).append(".").append(PARENT_UUID_FIELD)
							.append(") AS ").append(PARENT_HEX_UUID_FIELD)
						.append(" FROM ").append(APPLICATION_ROLES_GROUPS_TABLE)
						.append(" INNER JOIN ").append(TABLE)
						.append(" ON ").append(APPLICATION_ROLES_GROUPS_TABLE).append(".").append(EXTERNAL_ID_FIELD).append(" = ").append(TABLE).append(".").append(UUID_FIELD)
						.append(" WHERE ").append(APPLICATION_ROLES_GROUPS_TABLE).append(".").append(GroupDAOJdbc.EXTERNAL_ID_FIELD).append(" IN ").append(prepareUUIDPlaceHolders(groupMinimizedUUIDs.length))
						.append(" AND ").append(TABLE).append(".").append(ApplicationDAOJdbc.EXTERNAL_ID_FIELD).append(" = UNHEX(?)")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					int nextIndex = setStringsInStatement(statement, groupMinimizedUUIDs);
					statement.setString(nextIndex, applicationUUIDString);
					return statement;
				}

				@Override
				public List<ApplicationRole> interpretResultSet(ResultSet resultSet) throws SQLException {
					List<ApplicationRole> applicationRoles = null;

					applicationRoles = populateApplicationRoles(resultSet);

					return applicationRoles;
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error(
						"<< getApplicationRolesOfGroups() > There was a problem while trying to find the applicationRoles of the groups in the application: '{}'.",
						applicationUUID.toString());
			}
			throw e;
		}

		return applicationRoles;
	}

	public void removeGroupFromApplicationRole(ApplicationRole applicationRole, UUID groupUUID) throws CarbonException {
		// TODO Auto-generated method stub

	}

	// TODO: Merge this with the unordered one
	private List<ApplicationRole> populateParentApplicationRoles(ResultSet resultSet) throws SQLException {
		List<ApplicationRole> applicationRoles = new ArrayList<ApplicationRole>();

		while (resultSet.next()) {
			String uuidString = resultSet.getString(HEX_UUID_FIELD);
			String applicationUUIDString = resultSet.getString(ApplicationDAOJdbc.EXTERNAL_HEX_ID_FIELD);
			String parentUUIDString = resultSet.getString(PARENT_HEX_UUID_FIELD);
			String name = resultSet.getString(NAME_FIELD);
			String description = resultSet.getString(DESCRIPTION_FIELD);

			ApplicationRole applicationRole = new ApplicationRole();
			applicationRole.setUuid(uuidString);
			applicationRole.setName(name);
			applicationRole.setDescription(description);

			applicationRole.setApplicationUUID(AuthenticationUtil.restoreUUID(applicationUUIDString));

			if ( parentUUIDString != null ) {
				applicationRole.setParentUUID(AuthenticationUtil.restoreUUID(parentUUIDString));
			} else {
				applicationRole.setParentUUID(null);
			}

			applicationRoles.add(applicationRole);
		}

		return applicationRoles;
	}

	private List<ApplicationRole> populateApplicationRoles(ResultSet resultSet) throws SQLException {
		List<ApplicationRole> applicationRoles = new ArrayList<ApplicationRole>();

		while (resultSet.next()) {
			String uuidString = resultSet.getString(HEX_UUID_FIELD);
			String applicationUUIDString = resultSet.getString(ApplicationDAOJdbc.EXTERNAL_HEX_ID_FIELD);
			String parentUUIDString = resultSet.getString(PARENT_HEX_UUID_FIELD);
			String name = resultSet.getString(NAME_FIELD);
			String description = resultSet.getString(DESCRIPTION_FIELD);

			ApplicationRole applicationRole = new ApplicationRole();
			applicationRole.setUuid(uuidString);
			applicationRole.setName(name);
			applicationRole.setDescription(description);

			applicationRole.setApplicationUUID(AuthenticationUtil.restoreUUID(applicationUUIDString));

			if ( parentUUIDString != null ) {
				applicationRole.setParentUUID(AuthenticationUtil.restoreUUID(parentUUIDString));
			} else {
				applicationRole.setParentUUID(null);
			}

			applicationRoles.add(applicationRole);
		}

		return applicationRoles;
	}
}
