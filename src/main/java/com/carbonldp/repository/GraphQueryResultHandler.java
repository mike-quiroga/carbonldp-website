package com.carbonldp.repository;

import com.carbonldp.repository.txn.RepositoryRuntimeException;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.query.GraphQueryResult;
import org.eclipse.rdf4j.query.QueryEvaluationException;

public abstract class GraphQueryResultHandler implements StatementsHandler {
	public void handle( GraphQueryResult queryResult ) {
		try {
			start();
			boolean continueLoop = true;
			while ( queryResult.hasNext() && continueLoop ) {
				Statement statement = queryResult.next();
				continueLoop = handleStatement( statement );
			}
			end();
		} catch ( QueryEvaluationException e ) {
			throw new RepositoryRuntimeException( e );
		}
	}

	@Override
	public void start() {
	}

	@Override
	public void end() {
	}
}
