package com.carbonldp.repository;

import org.openrdf.model.Statement;
import org.openrdf.query.GraphQuery;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryEvaluationException;

import com.carbonldp.repository.txn.RepositoryRuntimeException;

public abstract class GraphQueryResultHandler implements StatementsHandler {
	public void handleQuery(GraphQuery query) {
		try {
			GraphQueryResult result = query.evaluate();
			start();
			boolean continueLoop = true;
			while (result.hasNext() && continueLoop) {
				Statement statement = result.next();
				continueLoop = handleStatement(statement);
			}
			end();
		} catch (QueryEvaluationException e) {
			throw new RepositoryRuntimeException(e);
		}
	}

	@Override
	public void start() {
	}

	@Override
	public void end() {
	}
}
