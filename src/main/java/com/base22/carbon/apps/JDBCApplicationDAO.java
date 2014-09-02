package com.base22.carbon.apps;

import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.base22.carbon.CarbonException;
import com.base22.carbon.DAOException;
import com.base22.carbon.authentication.AuthenticationUtil;
import com.base22.carbon.jdbc.QueryTransactionCallback;
import com.base22.carbon.jdbc.QueryTransactionTemplate;
import com.base22.carbon.jdbc.TransactionException;
import com.base22.carbon.jdbc.UpdateTransactionCallback;
import com.base22.carbon.jdbc.UpdateTransactionTemplate;
import com.base22.carbon.jdbc.DAOJdbc;

@Service("applicationDAO")
public class JDBCApplicationDAO extends DAOJdbc implements ApplicationDAO {

	public static final String TABLE = "applications";
	public static final String UUID_FIELD = "uuid";
	public static final String HEX_UUID_FIELD = "hex_uuid";
	public static final String DATASET_UUID_FIELD = "dataset";
	public static final String DATASET_HEX_UUID_FIELD = "hex_dataset";
	public static final String SLUG_FIELD = "slug";
	public static final String NAME_FIELD = "name";
	public static final String MASTER_KEY_FIELD = "master_key";

	public static final String EXTERNAL_ID_FIELD = "application_uuid";
	public static final String EXTERNAL_HEX_ID_FIELD = "application_hex_uuid";

	public static final String AGENTS_APPLICATIONS_TABLE = "agents_applications";

	public Application createApplication(final Application application) throws CarbonException {
		// Check if the masterKey is present
		if ( application.getMasterKey() == null ) {
			// It isn't, generate one
			try {
				application.setMasterKey(AuthenticationUtil.hashPassword(AuthenticationUtil.generateRandomSalt()));
			} catch (NoSuchAlgorithmException exception) {
				if ( LOG.isErrorEnabled() ) {
					LOG.error("A masterKey couldn't be generated because the hashing algorithm is not present.");
				}
				if ( LOG.isDebugEnabled() ) {
					exception.printStackTrace();
				}
				// TODO: Include in the carbon exception the proper parameters to generate an good error object
				throw new CarbonException("The application couldn't be created.");
			}
		}

		// Check if the UUID is present
		if ( application.getUuid() == null ) {
			// It isn't, generate one
			application.setUuid(UUID.randomUUID());
		}

		if ( application.getSlug() == null ) {
			application.setSlug(application.getUuidString());
		}

		// Prepare UUID
		final String uuidString = AuthenticationUtil.minimizeUUID(application.getUuid());

		// Prepare the datasetUUID for insertion
		final String datasetUUIDString = AuthenticationUtil.minimizeUUID(application.getDatasetUuid());

		// Insert the application
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
							.append(SLUG_FIELD)
							.append(", ")
							.append(NAME_FIELD)
							.append(", ")
							.append(DATASET_UUID_FIELD)
							.append(", ")
							.append(MASTER_KEY_FIELD)
						.append(") VALUES (UNHEX(?), ?, ?, UNHEX(?), ?)")
					;
					//@formatter:on
					return queryBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, uuidString);
					statement.setString(2, application.getSlug());
					statement.setString(3, application.getName());
					statement.setString(4, datasetUUIDString);
					statement.setString(5, application.getMasterKey());
					return statement;
				}
			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createApplication() > The application couldn't be created.");
			}
			throw e;
		}

		if ( insertionResult != 1 ) {
			// TODO: Can we get the reason in this case?
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createApplication() > The application couldn't be created.");
			}
			throw new DAOException("The application couldn't be created.");
		}

		return application;
	}

	public Application findByUUID(UUID uuid) throws CarbonException {
		Application application = null;

		final String uuidString = AuthenticationUtil.minimizeUUID(uuid);

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			application = template.execute(securityJDBCDataSource, new QueryTransactionCallback<Application>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT *, HEX(")
							.append(UUID_FIELD)
						.append(") AS ")
							.append(HEX_UUID_FIELD)
						.append(", HEX(")
							.append(DATASET_UUID_FIELD)
						.append(") AS ")
							.append(DATASET_HEX_UUID_FIELD)
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
				public Application interpretResultSet(ResultSet resultSet) throws SQLException {
					Application application = null;
					List<Application> applications = null;

					applications = populateApplications(resultSet);

					if ( applications != null ) {
						if ( ! applications.isEmpty() ) {
							application = (Application) applications.toArray()[0];
						}
					}

					return application;
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< findByUUID() > There was a problem while trying to find the application with UUID: '{}'.", uuid.toString());
			}
			throw e;
		}

		return application;
	}

	public Application findBySlug(final String slug) throws CarbonException {
		Application application = null;

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			application = template.execute(securityJDBCDataSource, new QueryTransactionCallback<Application>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT *, HEX(")
							.append(UUID_FIELD)
						.append(") AS ")
							.append(HEX_UUID_FIELD)
						.append(", HEX(")
							.append(DATASET_UUID_FIELD)
						.append(") AS ")
							.append(DATASET_HEX_UUID_FIELD)
						.append(" FROM ")
							.append(TABLE)
						.append(" WHERE ")
							.append(SLUG_FIELD)
						.append(" = ?")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, slug);
					return statement;
				}

				@Override
				public Application interpretResultSet(ResultSet resultSet) throws SQLException {
					Application application = null;
					List<Application> applications = null;

					applications = populateApplications(resultSet);

					if ( applications != null ) {
						if ( ! applications.isEmpty() ) {
							application = (Application) applications.toArray()[0];
						}
					}

					return application;
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< findBySlug() > There was a problem while trying to find the application with slug: '{}'.", slug);
			}
			throw e;
		}

		return application;
	}

	@Override
	public Application findByIdentifier(String identifier) throws CarbonException {
		Application application = null;

		if ( AuthenticationUtil.isUUIDString(identifier) ) {
			// The identifier is a uuid
			UUID applicationUUID = AuthenticationUtil.restoreUUID(identifier);
			application = findByUUID(applicationUUID);
		} else {
			// The identifier is a unique name
			application = findBySlug(identifier);
		}

		return application;
	}

	public boolean applicationExistsWithSlug(final String slug) throws CarbonException {
		boolean exists = false;

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			exists = template.execute(securityJDBCDataSource, new QueryTransactionCallback<Boolean>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT 1 FROM ")
							.append(TABLE)
						.append(" WHERE ")
							.append(SLUG_FIELD)
						.append(" = ?")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, slug);
					return statement;
				}

				@Override
				public Boolean interpretResultSet(ResultSet resultSet) throws SQLException {
					return resultSet.next();
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< applicationExistsWithSlug() > There was an error checking if the slug '{}' was attached to an Application.", slug);
			}
			throw e;
		}

		return exists;
	}

	public List<Application> getApplications() throws CarbonException {
		List<Application> applications = null;

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			applications = template.execute(securityJDBCDataSource, new QueryTransactionCallback<List<Application>>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT *, HEX(")
							.append(UUID_FIELD)
						.append(") AS ")
							.append(HEX_UUID_FIELD)
						.append(", HEX(")
							.append(DATASET_UUID_FIELD)
						.append(") AS ")
							.append(DATASET_HEX_UUID_FIELD)
						.append(" FROM ")
							.append(TABLE)
						.append(" WHERE  1")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					return statement;
				}

				@Override
				public List<Application> interpretResultSet(ResultSet resultSet) throws SQLException {
					List<Application> applications = null;

					applications = populateApplications(resultSet);

					return applications;
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getApplications() > The applications couldn't be retrieved.");
			}
			throw e;
		}

		return applications;
	}

	private List<Application> populateApplications(ResultSet resultSet) throws SQLException {
		List<Application> applications = new ArrayList<Application>();

		while (resultSet.next()) {
			String uuidString = resultSet.getString(HEX_UUID_FIELD);
			String dataset = resultSet.getString(DATASET_HEX_UUID_FIELD);
			String slug = resultSet.getString(SLUG_FIELD);
			String name = resultSet.getString(NAME_FIELD);
			String masterKey = null;
			try {
				resultSet.getString(MASTER_KEY_FIELD);
			} catch (SQLException exception) {
			}

			Application application = new Application();
			application.setUuid(uuidString);
			application.setDatasetUuid(dataset);
			application.setSlug(slug);
			application.setName(name);
			application.setMasterKey(masterKey);

			applications.add(application);
		}

		return applications;
	}
}
