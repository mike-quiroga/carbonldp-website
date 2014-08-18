package com.base22.carbon.security.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.security.dao.PrivilegeDAO;
import com.base22.carbon.security.exceptions.DAOException;
import com.base22.carbon.security.models.JDBCTransactionException;
import com.base22.carbon.security.models.Privilege;
import com.base22.carbon.security.utils.AuthenticationUtil;

@Service("privilegeDAO")
public class JDBCPrivilegeDAO extends JdbcDAO implements PrivilegeDAO {

	public static final String TABLE = "privileges";
	public static final String ID_FIELD = "id";
	public static final String NAME_FIELD = "name";

	public static final String EXTERNAL_ID_FIELD = "privilege_id";

	public static final String PRIVILEGES_ROLES_TABLE = "privileges_platform_roles";

	public HashSet<Privilege> getPrivilegesOfAgent(UUID agentUUID) throws CarbonException {
		HashSet<Privilege> privileges = null;

		final String uuidString = AuthenticationUtil.minimizeUUID(agentUUID);

		JDBCQueryTransactionTemplate template = new JDBCQueryTransactionTemplate();
		try {
			//@formatter:off
			privileges = template.execute(securityJDBCDataSource, new JDBCQueryTransactionCallback<HashSet<Privilege>>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT ")
							.append(TABLE).append(".").append(ID_FIELD).append(", ").append(TABLE).append(".").append(NAME_FIELD)
						.append(" FROM ")
							.append(JDBCAgentLoginDetailsDAO.AGENTS_ROLES_TABLE)
						.append(" INNER JOIN ")
							.append(PRIVILEGES_ROLES_TABLE)
						.append(" ON ")
							.append(JDBCAgentLoginDetailsDAO.AGENTS_ROLES_TABLE).append(".").append(JDBCPlatformRoleDAO.EXTERNAL_ID_FIELD).append(" = ").append(PRIVILEGES_ROLES_TABLE).append(".").append(JDBCPlatformRoleDAO.EXTERNAL_ID_FIELD)
						.append(" INNER JOIN ")
							.append(TABLE)
						.append(" ON ")
							.append(PRIVILEGES_ROLES_TABLE).append(".").append(EXTERNAL_ID_FIELD).append(" = ").append(TABLE).append(".").append(ID_FIELD)
						.append(" WHERE ")
							.append(JDBCAgentLoginDetailsDAO.AGENTS_ROLES_TABLE).append(".").append(JDBCAgentLoginDetailsDAO.EXTERNAL_ID_FIELD).append(" = UNHEX(?)")
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
				public HashSet<Privilege> interpretResultSet(ResultSet resultSet) throws SQLException {
					HashSet<Privilege> privileges = null;

					privileges = populatePrivileges(resultSet);

					return privileges;
				}

			});
		} catch (JDBCTransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getApplicationRolesOfGroup() > There was a problem while trying to find the privileges of the agent: '{}'.", agentUUID.toString());
			}
			throw e;
		}

		// Prepare the SQL Query
		StringBuilder sqlBuilder = new StringBuilder();

		//@formatter:off
		sqlBuilder
			.append("SELECT ")
				.append(TABLE).append(".").append(ID_FIELD).append(", ").append(TABLE).append(".").append(NAME_FIELD)
			.append(" FROM ")
				.append(JDBCAgentLoginDetailsDAO.AGENTS_ROLES_TABLE)
			.append(" INNER JOIN ")
				.append(PRIVILEGES_ROLES_TABLE)
			.append(" ON ")
				.append(JDBCAgentLoginDetailsDAO.AGENTS_ROLES_TABLE).append(".").append(JDBCPlatformRoleDAO.EXTERNAL_ID_FIELD).append(" = ").append(PRIVILEGES_ROLES_TABLE).append(".").append(JDBCPlatformRoleDAO.EXTERNAL_ID_FIELD)
			.append(" INNER JOIN ")
				.append(TABLE)
			.append(" ON ")
				.append(PRIVILEGES_ROLES_TABLE).append(".").append(EXTERNAL_ID_FIELD).append(" = ").append(TABLE).append(".").append(ID_FIELD)
			.append(" WHERE ")
				.append(JDBCAgentLoginDetailsDAO.AGENTS_ROLES_TABLE).append(".").append(JDBCAgentLoginDetailsDAO.EXTERNAL_ID_FIELD).append(" = UNHEX(?)")
		;
		//@formatter:on
		String sql = sqlBuilder.toString();

		Connection connection = null;

		try {
			// Retrieve the connection
			connection = securityJDBCDataSource.getConnection();

			// Prepare the statement
			PreparedStatement statement = connection.prepareStatement(sql);
			statement.setString(1, uuidString);

			// Execute the query
			ResultSet resultSet = statement.executeQuery();

			privileges = populatePrivileges(resultSet);

			resultSet.close();
			statement.close();

		} catch (SQLException exception) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getPrivilegesOfAgent -- The privileges of the agent: {}, couldn't be loaded.", agentUUID);
			}
			if ( LOG.isDebugEnabled() ) {
				exception.printStackTrace();
			}
			throw new DAOException("There was a problem loading the privileges of an agent.");
		} finally {
			closeConnection(connection);
		}

		return privileges;
	}

	private HashSet<Privilege> populatePrivileges(ResultSet resultSet) throws SQLException {
		HashSet<Privilege> privileges = new HashSet<Privilege>();

		while (resultSet.next()) {
			long id = resultSet.getLong(ID_FIELD);
			String name = resultSet.getString(NAME_FIELD);

			Privilege privilege = new Privilege(id, name);

			privileges.add(privilege);
		}

		return privileges;
	}
}
