package com.base22.carbon.security.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.base22.carbon.exceptions.CarbonException;
import com.base22.carbon.security.dao.URIObjectDAO;
import com.base22.carbon.security.exceptions.DAOException;
import com.base22.carbon.security.models.JDBCTransactionException;
import com.base22.carbon.security.models.URIObject;

@Service("uriObjectDAO")
public class JdbcURIObjectDAO extends JdbcDAO implements URIObjectDAO {

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
		} catch (JDBCTransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createURIObject() > The URIObject couldn't be created.");
			}
			throw e;
		}

		if ( insertionResult != 1 ) {
			// TODO: Can we get the reason in this case?
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createURIObject() > The URIObject couldn't be created.");
			}
			throw new DAOException("The URIObject couldn't be created.");
		}

		return uriObject;
	}

	@Override
	public URIObject findByURI(final String uri) throws CarbonException {
		URIObject uriObject = null;

		JDBCQueryTransactionTemplate template = new JDBCQueryTransactionTemplate();
		try {
			//@formatter:off
			uriObject = template.execute(securityJDBCDataSource, new JDBCQueryTransactionCallback<URIObject>() {
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
		} catch (JDBCTransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< findByURI() > There was a problem while trying to find the uriObject with uri: '{}'.", uri);
			}
			throw e;
		}

		return uriObject;

	}

	@Override
	public Set<URIObject> getByURIs(String... uris) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteURIObject(URIObject uriObject, final boolean deleteChildren) throws CarbonException {
		// Prepare UUID
		final String uri = uriObject.getURI();

		// Insert the uriObject
		int deletionResult;
		JDBCUpdateTransactionTemplate template = new JDBCUpdateTransactionTemplate();
		try {
			//@formatter:off
			deletionResult = template.execute(securityJDBCDataSource, new JDBCUpdateTransactionCallback() {
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
		} catch (JDBCTransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< deleteURIObject() > The URIObject couldn't be deleted.");
			}
			throw e;
		}

		if ( deletionResult != 1 ) {
			// TODO: Can we get the reason in this case?
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< deleteURIObject() > The URIObject couldn't be deleted.");
			}
			throw new DAOException("The URIObject couldn't be deleted.");
		}

		try {
			deleteChildrenURIObjects(uri);
		} catch (CarbonException e) {
			// TODO: FT
			throw e;
		}
	}

	private void deleteChildrenURIObjects(String uri) throws CarbonException {
		final String childrenURIBase = uri.endsWith("/") ? uri : uri.concat("/");
		// Insert the uriObject
		int deletionResult;
		JDBCUpdateTransactionTemplate template = new JDBCUpdateTransactionTemplate();
		try {
			//@formatter:off
			deletionResult = template.execute(securityJDBCDataSource, new JDBCUpdateTransactionCallback() {
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
		} catch (JDBCTransactionException e) {
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< deleteChildrenURIObjects() > The URIObject couldn't be deleted.");
			}
			throw e;
		}
	}

	@Override
	public void deleteURIObjects(URIObject... uriObjects) {
		// TODO Auto-generated method stub

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
