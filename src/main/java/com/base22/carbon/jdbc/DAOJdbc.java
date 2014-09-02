package com.base22.carbon.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public abstract class DAOJdbc {

	static final int SQL_NULL_TYPE = java.sql.Types.NULL;

	@Autowired
	protected DriverManagerDataSource securityJDBCDataSource;
	protected final Logger LOG;

	public DAOJdbc() {
		this.LOG = LoggerFactory.getLogger(this.getClass());
	}

	public void init() {
		if ( LOG.isTraceEnabled() ) {
			LOG.trace(">> init()");
		}
	}

	public void setSecurityJDBCDataSource(DriverManagerDataSource securityJDBCDataSource) {
		this.securityJDBCDataSource = securityJDBCDataSource;
	}

	protected void setStringOrNull(PreparedStatement statement, int index, String parameter) throws SQLException {
		if ( parameter == null ) {
			statement.setNull(index, SQL_NULL_TYPE);
		} else {
			statement.setString(index, parameter);
		}
	}

	protected String preparePlaceHolders(int length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length;) {
			builder.append("?");
			if ( ++i < length ) {
				builder.append(",");
			}
		}
		return builder.toString();
	}

	protected String prepareUUIDPlaceHolders(int length) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length;) {
			builder.append("UNHEX(?)");
			if ( ++i < length ) {
				builder.append(",");
			}
		}
		return builder.toString();
	}

	protected int setStringsInStatement(PreparedStatement preparedStatement, String[] strings) throws SQLException {
		return setStringsInStatement(preparedStatement, strings, 1);
	}

	protected int setStringsInStatement(PreparedStatement preparedStatement, String[] strings, int initialIndex) throws SQLException {
		int i;
		for (i = 0; i < strings.length; i++) {
			preparedStatement.setString(i + initialIndex, strings[i]);
		}
		return i + initialIndex;
	}

	protected void closeConnection(Connection connection) {
		if ( connection != null ) {
			try {
				connection.close();
			} catch (SQLException exception) {
				if ( LOG.isErrorEnabled() ) {
					LOG.error("<< The connection couldn't be closed.");
				}
				if ( LOG.isDebugEnabled() ) {
					exception.printStackTrace();
				}
			}
		}
	}
}
