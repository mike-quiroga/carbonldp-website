package com.base22.carbon.security.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.base22.carbon.security.models.JDBCTransactionException;

public abstract class JDBCTransactionTemplate {
	protected final Logger LOG;

	public JDBCTransactionTemplate() {
		this.LOG = LoggerFactory.getLogger(this.getClass());
	}

	protected Connection getConnection(DriverManagerDataSource dataSource) throws JDBCTransactionException {
		Connection connection = null;
		try {
			connection = dataSource.getConnection();
		} catch (SQLException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx getConnection() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< getConnection() > A connection couldn't be retrieved.");
			}
			throw new JDBCTransactionException("A connection coulnd't be retrieved.");
		}
		return connection;
	}

	protected PreparedStatement createPreparedStatement(Connection connection, String query) throws JDBCTransactionException {
		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(query);
		} catch (SQLException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx createPreparedStatement() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< createPreparedStatement() > A prepared statement couldn't be created.");
			}
			throw new JDBCTransactionException("A prepared statement couldn't be created.");
		}
		return statement;
	}

	protected void closeConnection(Connection connection) {
		try {
			connection.close();
		} catch (SQLException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx closeConnection() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< closeConnection() > The connection couldn't be closed.");
			}
		}
	}

	protected void closeStatement(PreparedStatement statement) {
		try {
			statement.close();
		} catch (SQLException e) {
			if ( LOG.isDebugEnabled() ) {
				LOG.debug("xx closeStatement() > Exception Stacktrace:", e);
			}
			if ( LOG.isErrorEnabled() ) {
				LOG.error("<< closeStatement() > The statement couldn't be closed.");
			}
		}
	}

}
