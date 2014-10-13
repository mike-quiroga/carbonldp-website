package com.base22.carbon.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class UpdateTransactionTemplate extends TransactionTemplate {

	protected final DriverManagerDataSource dataSource;
	protected final Connection connection;
	private final List<UpdateTransactionCallback> callbacks;

	public UpdateTransactionTemplate(DriverManagerDataSource dataSource) throws TransactionException {
		this.dataSource = dataSource;
		this.connection = getConnection(dataSource);
		this.callbacks = new ArrayList<UpdateTransactionCallback>();
	}

	public void addCallback(UpdateTransactionCallback callback) throws TransactionException {
		callbacks.add(callback);
	}

	public void execute() throws TransactionException {
		execute(true);
	}

	public void execute(boolean autoCommit) throws TransactionException {
		if ( isClosed() ) {
			throw new TransactionException("The connection is already closed.");
		}

		try {
			for (UpdateTransactionCallback callback : callbacks) {
				StringBuilder queryBuilder = new StringBuilder();
				queryBuilder = callback.prepareSQLQuery(queryBuilder);
				String query = queryBuilder.toString();

				PreparedStatement statement = createPreparedStatement(connection, query);

				try {
					statement = callback.prepareStatement(statement);

					if ( callback.isBatch() ) {
						executeBatch(statement);
					} else {
						executeUpdate(statement);
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
			}

			if ( autoCommit ) {
				commit();
			}

		} catch (TransactionException e) {
			if ( autoCommit ) {
				closeConnection(connection);
			}
			throw e;
		} finally {
			if ( ! autoCommit ) {
				closeConnection(connection);
			}
		}
	}

	public void execute(UpdateTransactionCallback callback) throws TransactionException {
		addCallback(callback);
		execute();
	}

	public void commit() throws TransactionException {
		if ( isClosed() ) {
			throw new TransactionException("The connection is already closed.");
		}

		try {
			connection.commit();
		} catch (SQLException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx commit() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< commit() > The connection couldn't be commited.");
			}

			try {
				connection.rollback();
			} catch (SQLException rollbackE) {
				if ( LOG.isDebugEnabled() ) {
					LOG.debug("xx commit() > Exception Stacktrace:", rollbackE);
				}
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< commit() > The connection couldn't be rolledback.");
				}
				throw new TransactionException("The connection couldn't be rolledback.");
			}

			throw new TransactionException("The connection couldn't be commited.");
		} finally {

			closeConnection(connection);
		}
	}

	public boolean isClosed() throws TransactionException {
		try {
			return connection.isClosed();
		} catch (SQLException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx isClosed() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< isClosed() > The connection couldn't be checked.");
			}
			throw new TransactionException("The connection couldn't be checked.");
		}
	}

	private void executeUpdate(PreparedStatement statement) throws TransactionException {
		try {
			statement.executeUpdate();
		} catch (SQLException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx executeStatement() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< executeStatement() > The statement couldn't be executed.");
			}
			throw new TransactionException("The statement couldn't be executed.");
		}
	}

	private void executeBatch(PreparedStatement statement) throws TransactionException {
		try {
			int[] results = statement.executeBatch();
			for (int result : results) {
				if ( result == PreparedStatement.EXECUTE_FAILED ) {
					throw new TransactionException("One of the statements in the batch failed to execute successfully.");
				}
			}
		} catch (SQLTimeoutException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx executeBatch() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< executeBatch() > The statement timed out.");
			}
			throw new TransactionException("The statement timed out.");
		} catch (SQLException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx executeStatement() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< executeStatement() > The statement couldn't be executed.");
			}
			throw new TransactionException("The statement couldn't be executed.");
		}
	}

}
