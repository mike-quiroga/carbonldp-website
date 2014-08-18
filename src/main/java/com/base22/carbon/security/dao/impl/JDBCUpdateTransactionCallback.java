package com.base22.carbon.security.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface JDBCUpdateTransactionCallback {
	StringBuilder prepareSQLQuery(StringBuilder queryBuilder);

	PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException;
}
