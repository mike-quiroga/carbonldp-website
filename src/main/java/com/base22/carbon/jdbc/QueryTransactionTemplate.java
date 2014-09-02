package com.base22.carbon.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class QueryTransactionTemplate extends TransactionTemplate {

	public <T> T execute(DriverManagerDataSource dataSource, QueryTransactionCallback<T> action) throws TransactionException {
		T result = null;

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder = action.prepareSQLQuery(queryBuilder);
		String query = queryBuilder.toString();

		Connection connection = getConnection(dataSource);

		try {
			PreparedStatement statement = createPreparedStatement(connection, query);

			try {
				statement = action.prepareStatement(statement);

				ResultSet resultSet = executeStatement(statement);

				try {
					result = action.interpretResultSet(resultSet);
				} catch (SQLException e) {
					if ( LOG.isDebugEnabled() ) {
						LOG.debug("xx execute() > Exception Stacktrace:", e);
					}
					if ( LOG.isErrorEnabled() ) {
						LOG.error("<< execute() > The resultSet couldn't be interpreted.");
					}
					throw new TransactionException("The resultSet couldn't be interpreted.");
				} finally {
					closeResultSet(resultSet);
				}
			} catch (SQLException e) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx execute() > Exception Stacktrace:", e);
				}
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< execute() > The statement couldn't be prepared.");
				}
				throw new TransactionException("The statement couldn't be prepared.");

			} catch (TransactionException e) {
				throw e;
			} finally {
				closeStatement(statement);
			}
		} catch (TransactionException e) {
			throw e;
		} finally {
			closeConnection(connection);
		}

		return result;
	}

	private ResultSet executeStatement(PreparedStatement statement) throws TransactionException {
		ResultSet resultSet = null;
		try {
			resultSet = statement.executeQuery();
		} catch (SQLException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx executeStatement() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< executeStatement() > The statement couldn't be executed.");
			}
			throw new TransactionException("The statement couldn't be executed.");
		}
		return resultSet;
	}

	private void closeResultSet(ResultSet resultSet) {
		try {
			resultSet.close();
		} catch (SQLException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx closeResultSet() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< closeResultSet() > The resultSet couldn't be closed.");
			}
		}
	}
}
