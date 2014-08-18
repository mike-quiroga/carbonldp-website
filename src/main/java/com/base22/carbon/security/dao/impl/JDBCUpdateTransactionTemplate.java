package com.base22.carbon.security.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.base22.carbon.security.models.JDBCTransactionException;

public class JDBCUpdateTransactionTemplate extends JDBCTransactionTemplate {

	public int execute(DriverManagerDataSource dataSource, JDBCUpdateTransactionCallback action) throws JDBCTransactionException {
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
				throw new JDBCTransactionException("The statement couldn't be prepared.");

			} catch (JDBCTransactionException e) {
				throw e;
			} finally {
				closeStatement(statement);
			}
		} catch (JDBCTransactionException e) {
			throw e;
		} finally {
			closeConnection(connection);
		}

		return insertionResult;
	}

	private int executeStatement(PreparedStatement statement) throws JDBCTransactionException {
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
			throw new JDBCTransactionException("The statement couldn't be executed.");
		}
		return insertionResult;
	}

}
