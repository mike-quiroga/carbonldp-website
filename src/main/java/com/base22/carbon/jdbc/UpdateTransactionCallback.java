package com.base22.carbon.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface UpdateTransactionCallback {
	boolean isBatch();

	StringBuilder prepareSQLQuery(StringBuilder queryBuilder);

	PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException;
}