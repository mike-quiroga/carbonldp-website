package com.base22.carbon.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class BatchUpdateTransactionCallback implements UpdateTransactionCallback {

	@Override
	public boolean isBatch() {
		return true;
	}

	@Override
	public abstract StringBuilder prepareSQLQuery(StringBuilder queryBuilder);

	@Override
	public abstract PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException;

}
