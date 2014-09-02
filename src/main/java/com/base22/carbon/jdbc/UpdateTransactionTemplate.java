package com.base22.carbon.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class UpdateTransactionTemplate extends TransactionTemplate {

	public int execute(DriverManagerDataSource dataSource, UpdateTransactionCallback action) throws TransactionException {
		int insertionResult;

		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder = action.prepareSQLQuery(queryBuilder);
		String query = queryBuilder.toString();

		Connection connection = getConnection(dataSource);

		try {
			PreparedStatement statement = createPreparedStatement(connection, query);

			try {
				statement = action.prepareStatement(statement);

				insertionResult = executeStatement(statement);
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

		return insertionResult;
	}

	private int executeStatement(PreparedStatement statement) throws TransactionException {
		int insertionResult;
		try {
			insertionResult = statement.executeUpdate();
		} catch (SQLException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx executeStatement() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< executeStatement() > The statement couldn't be executed.");
			}
			throw new TransactionException("The statement couldn't be executed.");
		}
		return insertionResult;
	}

}
