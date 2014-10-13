package com.base22.carbon.authorization;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.base22.carbon.CarbonException;
import com.base22.carbon.DAOException;
import com.base22.carbon.agents.Agent;
import com.base22.carbon.agents.AgentDAOJdbc;
import com.base22.carbon.jdbc.DAOJdbc;
import com.base22.carbon.jdbc.QueryTransactionCallback;
import com.base22.carbon.jdbc.QueryTransactionTemplate;
import com.base22.carbon.jdbc.SingleUpdateTransactionCallback;
import com.base22.carbon.jdbc.TransactionException;
import com.base22.carbon.jdbc.UpdateTransactionTemplate;

@Service("roleDAO")
public class PlatformRoleDAOJdbc extends DAOJdbc implements PlatformRoleDAO {
	public static final String TABLE = "platform_roles";
	public static final String ID_FIELD = "id";
	public static final String NAME_FIELD = "name";
	public static final String DESCRIPTION_FIELD = "description";

	public static final String EXTERNAL_ID_FIELD = "platform_role_id";

	public static final String AGENTS_ROLES_TABLE = "agents_platform_roles";
	public static final String PRIVILEGES_ROLES_TABLE = "privileges_roles";

	@Override
	public void createRole(final PlatformRole role) throws CarbonException {
		if ( role.getName() == null ) {
			throw new DAOException("The name of the platformRole can't be null.");
		}

		UpdateTransactionTemplate template = new UpdateTransactionTemplate(securityJDBCDataSource);
		try {
			//@formatter:off
			template.execute(new SingleUpdateTransactionCallback() {
				//@formatter:on
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder queryBuilder) {
					//@formatter:off
					queryBuilder
						.append("INSERT INTO ")
						.append(TABLE)
						.append(" (")
							.append(NAME_FIELD)
							.append(", ")
							.append(DESCRIPTION_FIELD)
						.append(") VALUES (?, ?)")
					;
					//@formatter:on
					return queryBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, role.getName());
					setStringOrNull(statement, 2, role.getDescription());

					return statement;
				}
			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createRole() > The platformRole couldn't be created.");
			}
			throw e;
		}
	}

	@Override
	public PlatformRole findByName(final String name) throws CarbonException {
		PlatformRole role = null;

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			role = template.execute(securityJDBCDataSource, new QueryTransactionCallback<PlatformRole>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT *  FROM ")
							.append(TABLE)
						.append(" WHERE ")
							.append(NAME_FIELD).append(" = ?")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, name);
					return statement;
				}

				@Override
				public PlatformRole interpretResultSet(ResultSet resultSet) throws SQLException {
					List<PlatformRole> roles = null;

					roles = populatePlatformRoles(resultSet);

					if ( roles.isEmpty() ) {
						return null;
					}

					return roles.get(0);
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< findByName() > There was a problem while trying to find the platformRole with name: '{}'.", name);
			}
			throw e;
		}

		return role;
	}

	@Override
	public PlatformRole findByID(final long id) throws CarbonException {
		PlatformRole role = null;

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			role = template.execute(securityJDBCDataSource, new QueryTransactionCallback<PlatformRole>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT *  FROM ")
							.append(TABLE)
						.append(" WHERE ")
							.append(ID_FIELD).append(" = ?")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setLong(1, id);
					return statement;
				}

				@Override
				public PlatformRole interpretResultSet(ResultSet resultSet) throws SQLException {
					List<PlatformRole> roles = null;

					roles = populatePlatformRoles(resultSet);

					if ( roles.isEmpty() ) {
						return null;
					}

					return roles.get(0);
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< findByID() > There was a problem while trying to find the platformRole with id: '{}'.", String.valueOf(id));
			}
			throw e;
		}

		return role;

	}

	// === Agent Related Methods

	@Override
	public void addAgentToRole(Agent agent, PlatformRole role) throws CarbonException {
		final String agentUUIDString = agent.getMinimizedUuidString();
		final long roleID = role.getID();

		UpdateTransactionTemplate template = new UpdateTransactionTemplate(securityJDBCDataSource);
		try {
			//@formatter:off
			template.execute(new SingleUpdateTransactionCallback() {
				//@formatter:on
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder queryBuilder) {
					//@formatter:off
					queryBuilder
						.append("INSERT INTO ")
						.append(AGENTS_ROLES_TABLE)
						.append(" (")
							.append(AgentDAOJdbc.EXTERNAL_ID_FIELD)
							.append(", ")
							.append(EXTERNAL_ID_FIELD)
						.append(") VALUES (UNHEX(?), ?)")
					;
					//@formatter:on
					return queryBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, agentUUIDString);
					statement.setLong(2, roleID);

					return statement;
				}
			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< addAgentToRole() > The agent with UUID: '{}', couldn't be added to the platformRole with ID: '{}'.", agent.getUuidString(),
						String.valueOf(role.getID()));
			}
			throw e;
		}

	}

	@Override
	public void removeAgentFromRole(Agent agent, PlatformRole role) throws CarbonException {
		// TODO Auto-generated method stub
	}

	// === End: Agent Related Methods

	private List<PlatformRole> populatePlatformRoles(ResultSet resultSet) throws SQLException {
		List<PlatformRole> roles = new ArrayList<PlatformRole>();

		if ( resultSet.next() ) {
			long id = resultSet.getLong(ID_FIELD);
			String name = resultSet.getString(NAME_FIELD);
			String description = resultSet.getString(DESCRIPTION_FIELD);

			PlatformRole role = new PlatformRole();

			role.setID(id);
			role.setName(name);
			role.setDescription(description);

			roles.add(role);
		}

		return roles;

	}
}
