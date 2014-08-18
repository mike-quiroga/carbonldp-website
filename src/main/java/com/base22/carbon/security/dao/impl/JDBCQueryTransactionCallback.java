package com.base22.carbon.security.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface JDBCQueryTransactionCallback<T> {
	StringBuilder prepareSQLQuery(StringBuilder queryBuilder);

	PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException;

	T interpretResultSet(ResultSet resultSet) throws SQLException;
}
