package com.base22.carbon.ldp;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.base22.carbon.CarbonException;
import com.base22.carbon.jdbc.BatchUpdateTransactionCallback;
import com.base22.carbon.jdbc.DAOJdbc;
import com.base22.carbon.jdbc.QueryTransactionCallback;
import com.base22.carbon.jdbc.QueryTransactionTemplate;
import com.base22.carbon.jdbc.SingleUpdateTransactionCallback;
import com.base22.carbon.jdbc.TransactionException;
import com.base22.carbon.jdbc.UpdateTransactionTemplate;
import com.base22.carbon.ldp.models.URIObject;

@Service("uriObjectDAO")
public class URIObjectDAOJdbc extends DAOJdbc implements URIObjectDAO {

	public static final String TABLE = "uri_objects";
	public static final String UUID_FIELD = "uuid";
	public static final String HEX_UUID_FIELD = "hex_uuid";
	public static final String URI_FIELD = "uri";

	@Override
	public URIObject createURIObject(URIObject uriObject) throws CarbonException {
		if ( uriObject.getUuid() == null ) {
			// The UUID wasn't set, create one
			UUID uuid = UUID.randomUUID();
			uriObject.setUuid(uuid);
		}

		// Prepare UUID
		final String uuidString = uriObject.getMinimizedUuidString();
		final String uri = uriObject.getURI();

		// Insert the uriObject
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
									.append(UUID_FIELD)
									.append(", ")
									.append(URI_FIELD)
								.append(") VALUES (UNHEX(?), ?)")
							;
							//@formatter:on
					return queryBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, uuidString);
					statement.setString(2, uri);
					return statement;
				}
			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createURIObject() > The URIObject couldn't be created.");
			}
			throw e;
		}

		return uriObject;
	}

	public List<URIObject> createURIObjects(final List<URIObject> uriObjects) throws CarbonException {
		for (URIObject uriObject : uriObjects) {
			if ( uriObject.getUuid() == null ) {
				// The UUID wasn't set, create one
				UUID uuid = UUID.randomUUID();
				uriObject.setUuid(uuid);
			}
		}

		// Insert the uriObject
		UpdateTransactionTemplate template = new UpdateTransactionTemplate(securityJDBCDataSource);

		//@formatter:off
		template.execute(new BatchUpdateTransactionCallback() {
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
						.append(URI_FIELD)
					.append(") VALUES (UNHEX(?), ?)")
				;
				//@formatter:on
				return queryBuilder;
			}

			@Override
			public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
				for (URIObject uriObject : uriObjects) {
					String uuidString = uriObject.getMinimizedUuidString();
					String uri = uriObject.getURI();

					statement.setString(1, uuidString);
					statement.setString(2, uri);
					statement.addBatch();
				}
				return statement;
			}
		});

		return uriObjects;
	}

	public boolean uriObjectsExist(final List<String> uris) throws CarbonException {
		QueryTransactionTemplate template = new QueryTransactionTemplate();

		Boolean exist = null;
		try {
			//@formatter:off
			exist = template.execute(securityJDBCDataSource, new QueryTransactionCallback<Boolean>() {
				//@formatter:on
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder queryBuilder) {
					//@formatter:off
					queryBuilder
						.append("SELECT COUNT(1) FROM ")
							.append(TABLE)
						.append(" WHERE ")
							.append(URI_FIELD)
						.append(" IN (").append(preparePlaceHolders(uris.size())).append(")")
					;
					//@formatter:on
					return queryBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					setStringsInStatement(statement, uris.toArray(new String[uris.size()]));
					return statement;
				}

				@Override
				public Boolean interpretResultSet(ResultSet resultSet) throws SQLException {
					Boolean exist = false;
					if ( resultSet.next() ) {
						if ( resultSet.getInt(1) >= 1 ) {
							exist = true;
						}
					}
					return exist;
				}

			});
		} catch (CarbonException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< uriObjectsAlreadyExist() > There was a problem while trying to check for existence the uris: '{}'.", uris.toString());
			}
			throw e;
		}

		if ( exist == null ) {
			exist = false;
		}

		return exist;
	}

	@Override
	public URIObject findByURI(final String uri) throws CarbonException {
		URIObject uriObject = null;

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			uriObject = template.execute(securityJDBCDataSource, new QueryTransactionCallback<URIObject>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT *, HEX(")
							.append(UUID_FIELD)
						.append(") AS ")
							.append(HEX_UUID_FIELD)
						.append(" FROM ")
							.append(TABLE)
						.append(" WHERE ")
							.append(URI_FIELD)
						.append(" = ?")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					statement.setString(1, uri);
					return statement;
				}

				@Override
				public URIObject interpretResultSet(ResultSet resultSet) throws SQLException {
					URIObject uriObject = null;
					Set<URIObject> uriObjects = null;

					uriObjects = populateURIObjects(resultSet);

					if ( uriObjects != null ) {
						if ( ! uriObjects.isEmpty() ) {
							uriObject = (URIObject) uriObjects.toArray()[0];
						}
					}

					return uriObject;
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< findByURI() > There was a problem while trying to find the uriObject with uri: '{}'.", uri);
			}
			throw e;
		}

		return uriObject;

	}

	@Override
	public List<URIObject> getByURIs(final List<String> uris) throws CarbonException {
		List<URIObject> uriObjects = null;

		QueryTransactionTemplate template = new QueryTransactionTemplate();
		try {
			//@formatter:off
			uriObjects = template.execute(securityJDBCDataSource, new QueryTransactionCallback<List<URIObject>>() {
				//@formatter:off
				@Override
				public StringBuilder prepareSQLQuery(StringBuilder sqlBuilder) {
					//@formatter:off
					sqlBuilder
						.append("SELECT *, HEX(")
							.append(UUID_FIELD)
						.append(") AS ")
							.append(HEX_UUID_FIELD)
						.append(" FROM ")
							.append(TABLE)
						.append(" WHERE ")
							.append(URI_FIELD)
						.append(" IN (").append(preparePlaceHolders(uris.size())).append(")")
					;
					//@formatter:on
					return sqlBuilder;
				}

				@Override
				public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
					setStringsInStatement(statement, uris.toArray(new String[uris.size()]));
					return statement;
				}

				@Override
				public List<URIObject> interpretResultSet(ResultSet resultSet) throws SQLException {
					List<URIObject> uriObjects = null;

					uriObjects = populateURIObjectsList(resultSet);

					return uriObjects;
				}

			});
		} catch (TransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getByURIs() > There was a problem while trying to get the URIObjects.");
			}
			throw e;
		}

		return uriObjects;
	}

	@Override
	public void deleteURIObject(URIObject uriObject, final boolean deleteChildren) throws CarbonException {
		UpdateTransactionTemplate template = new UpdateTransactionTemplate(securityJDBCDataSource);
		deleteURIObject(uriObject, deleteChildren, template);
		template.execute();
	}

	public void deleteURIObject(URIObject uriObject, final boolean deleteChildren, UpdateTransactionTemplate template) throws CarbonException {
		// Prepare UUID
		final String uri = uriObject.getURI();

		//@formatter:off
		template.addCallback(new SingleUpdateTransactionCallback() {
			//@formatter:on
			@Override
			public StringBuilder prepareSQLQuery(StringBuilder queryBuilder) {
				//@formatter:off
				queryBuilder
					.append("DELETE FROM ")
					.append(TABLE)
					.append(" WHERE ")
						.append(URI_FIELD)
						.append(" = ")
						.append("?")
				;
				//@formatter:on
				return queryBuilder;
			}

			@Override
			public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
				statement.setString(1, uri);
				return statement;
			}
		});

		if ( deleteChildren ) {
			deleteChildrenURIObjects(uri, template);
		}
	}

	private void deleteChildrenURIObjects(String uri, UpdateTransactionTemplate template) throws CarbonException {
		final String childrenURIBase = uri.endsWith("/") ? uri : uri.concat("/");

		//@formatter:off
		template.addCallback(new SingleUpdateTransactionCallback() {
			//@formatter:on
			@Override
			public StringBuilder prepareSQLQuery(StringBuilder queryBuilder) {
				//@formatter:off
				queryBuilder
					.append("DELETE FROM ")
					.append(TABLE)
					.append(" WHERE ")
						.append(URI_FIELD)
						.append(" LIKE ")
						.append("?")
				;
				//@formatter:on
				return queryBuilder;
			}

			@Override
			public PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException {
				statement.setString(1, childrenURIBase + "%");
				return statement;
			}
		});
	}

	@Override
	public void deleteURIObjects(URIObject... uriObjects) {
		// TODO Auto-generated method stub

	}

	private List<URIObject> populateURIObjectsList(ResultSet resultSet) throws SQLException {
		List<URIObject> uriObjects = new ArrayList<URIObject>();

		while (resultSet.next()) {
			String uuidString = resultSet.getString(HEX_UUID_FIELD);
			String uri = resultSet.getString(URI_FIELD);

			URIObject uriObject = new URIObject(uri);
			uriObject.setUuid(uuidString);

			uriObjects.add(uriObject);
		}

		return uriObjects;
	}

	private Set<URIObject> populateURIObjects(ResultSet resultSet) throws SQLException {
		Set<URIObject> uriObjects = new HashSet<URIObject>();

		while (resultSet.next()) {
			String uuidString = resultSet.getString(HEX_UUID_FIELD);
			String uri = resultSet.getString(URI_FIELD);

			URIObject uriObject = new URIObject(uri);
			uriObject.setUuid(uuidString);

			uriObjects.add(uriObject);
		}

		return uriObjects;
	}

}
