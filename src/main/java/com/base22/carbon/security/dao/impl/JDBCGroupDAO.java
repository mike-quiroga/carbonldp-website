package com.base22.carbon.security.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.security.dao.GroupDAO;
import com.base22.carbon.security.exceptions.DAOException;
import com.base22.carbon.security.models.Group;
import com.base22.carbon.security.models.JDBCTransactionException;
import com.base22.carbon.security.utils.AuthenticationUtil;

@Service("groupDAO")
public class JDBCGroupDAO extends JdbcDAO implements GroupDAO {

	public static final String TABLE = "groups";
	public static final String UUID_FIELD = "uuid";
	public static final String HEX_UUID_FIELD = "hex_uuid";
	public static final String PARENT_UUID_FIELD = "parent_uuid";
	public static final String PARENT_HEX_UUID_FIELD = "parent_hex_uuid";
	public static final String NAME_FIELD = "name";
	public static final String DESCRIPTION_FIELD = "description";

	public static final String EXTERNAL_ID_FIELD = "group_uuid";

	public static final String AGENTS_GROUPS_TABLE = "agents_groups";

	public void createGroup(Group group) {
		// TODO Auto-generated method stub

	}

	public void createChildGroup(Group parentGroup, Group childGroup) {
		// TODO Auto-generated method stub

	}

	public Group findByUUID(UUID groupUUID) {
		// TODO Auto-generated method stub
		return null;
	}

	public HashSet<Group> getGroups() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addAgentToGroup(Group group, UUID agentUUID) throws CarbonException {
		final String groupUUIDString = group.getMinimizedUuidString();
		final String agentUUIDString = AuthenticationUtil.minimizeUUID(agentUUID);

		int insertionResult;
		JDBCUpdateTransactionTemplate template = new JDBCUpdateTransactionTemplate();
		try {
			//@formatter:off
			insertionResult = template.execute(securityJDBCDataSource, new JDBCUpdateTransactionCallback() {
				//@formatter:on
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder queryBuilder) {
					//@formatter:off
					queryBuilder
						.append("INSERT INTO ").append(AGENTS_GROUPS_TABLE)
							.append("(").append(JDBCAgentLoginDetailsDAO.EXTERNAL_ID_FIELD).append(", ").append(EXTERNAL_ID_FIELD).append(")")
						.append("VALUES (UNHEX(?), UNHEX(?))")
					;
					//@formatter:on
					return queryBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, agentUUIDString);
					statement.setString(2, groupUUIDString);
					return statement;
				}
			});
		} catch (JDBCTransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< addAgentToGroup() > The agent: '{}', couldn't be added to the group: '{}'.", agentUUID.toString(), group.getUuidString());
			}
			throw e;
		}

		if ( insertionResult != 1 ) {
			// TODO: Can we get the reason in this case?
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< addAgentToGroup() > The agent: '{}', couldn't be added to the group: '{}'.", agentUUID.toString(), group.getUuidString());
			}
			throw new DAOException("The agent couldn't be added to the group.");
		}
	}

	public HashSet<Group> getImmediateGroupsOfAgent(UUID agentUUID) throws CarbonException {
		HashSet<Group> groups = new HashSet<Group>();

		final String agentUUIDString = AuthenticationUtil.minimizeUUID(agentUUID);

		JDBCQueryTransactionTemplate template = new JDBCQueryTransactionTemplate();
		try {
			//@formatter:off
			groups = template.execute(securityJDBCDataSource, new JDBCQueryTransactionCallback<HashSet<Group>>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT ").append(TABLE).append(".*, HEX(").append(TABLE).append(".").append(UUID_FIELD).append(") AS ").append(HEX_UUID_FIELD)
						.append(" FROM ").append(AGENTS_GROUPS_TABLE)
						.append(" INNER JOIN ").append(TABLE)
						.append(" ON ").append(AGENTS_GROUPS_TABLE).append(".").append(EXTERNAL_ID_FIELD).append(" = ").append(TABLE).append(".").append(UUID_FIELD)
						.append(" WHERE ").append(AGENTS_GROUPS_TABLE).append(".").append(JDBCAgentLoginDetailsDAO.EXTERNAL_ID_FIELD).append(" = UNHEX(?)")
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
				public HashSet<Group> interpretResultSet(ResultSet resultSet) throws SQLException {
					HashSet<Group> groups = null;

					groups = populateGroups(resultSet);

					return groups;
				}

			});
		} catch (JDBCTransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getImmediateGroupsOfAgent() > There was a problem while trying to find the immediate groups of the agent: '{}'.",
						agentUUID.toString());
			}
			throw e;
		}

		return groups;
	}

	public HashSet<Group> getAllGroupsOfAgent(UUID agentUUID) throws CarbonException {
		HashSet<Group> groups = new HashSet<Group>();
		HashSet<Group> parentGroups = new HashSet<Group>();

		// Fetch the immediateGroups related to the agent
		HashSet<Group> immediateGroups = getImmediateGroupsOfAgent(agentUUID);

		// If there are no immediate groups the following is irrelevant
		if ( immediateGroups.isEmpty() ) {
			return groups;
		}

		for (final Group immediateGroup : immediateGroups) {
			JDBCQueryTransactionTemplate template = new JDBCQueryTransactionTemplate();
			try {
				//@formatter:off
				parentGroups = template.execute(securityJDBCDataSource, new JDBCQueryTransactionCallback<HashSet<Group>>() {
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
							    .append(") vars, groups h ")
							    .append("WHERE @r IS NOT NULL ")
							.append(") T1 ")
							.append("JOIN ").append(TABLE).append(" T2 ")
							.append("ON T1._").append(UUID_FIELD).append(" = T2.").append(UUID_FIELD)
							.append(" ORDER BY T1.lvl DESC")
						 ;
						//@formatter:on
						return sqlBuilder;
					}

					@Override
					public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
						statement.setString(1, immediateGroup.getMinimizedUuidString());
						return statement;
					}

					@Override
					public HashSet<Group> interpretResultSet(ResultSet resultSet) throws SQLException {
						HashSet<Group> groups = null;

						groups = populateGroups(resultSet);

						return groups;
					}

				});
			} catch (JDBCTransactionException e) {
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< getAllGroupsOfAgent() > There was a problem while trying to find the immediate groups of the agent: '{}'.",
							agentUUID.toString());
				}
				throw e;
			}

			groups.addAll(parentGroups);
		}

		return groups;
	}

	public void removeAgentFromGroup(Group group, UUID agentUUID) {
		// TODO Auto-generated method stub

	}

	private HashSet<Group> populateGroups(ResultSet resultSet) throws SQLException {
		HashSet<Group> groups = new HashSet<Group>();

		while (resultSet.next()) {
			String uuidString = resultSet.getString(HEX_UUID_FIELD);
			String name = resultSet.getString(NAME_FIELD);
			String description = resultSet.getString(DESCRIPTION_FIELD);

			Group group = new Group();
			group.setUuid(uuidString);
			group.setName(name);
			group.setDescription(description);

			groups.add(group);
		}

		return groups;
	}
}
