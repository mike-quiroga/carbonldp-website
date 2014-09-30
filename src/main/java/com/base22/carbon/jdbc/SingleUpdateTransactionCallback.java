package com.base22.carbon.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SingleUpdateTransactionCallback implements UpdateTransactionCallback {

	@Override
	public boolean isBatch() {
		return false;
	}

	@Override
	public abstract StringBuilder prepareSQLQuery(StringBuilder queryBuilder);

	@Override
	public abstract PreparedStatement prepareStatement(PreparedStatement statement) throws SQLException;

}
