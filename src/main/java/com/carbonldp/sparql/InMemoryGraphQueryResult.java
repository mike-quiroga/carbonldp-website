package com.carbonldp.sparql;

import com.google.common.collect.ImmutableMap;
import org.openrdf.model.Statement;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryEvaluationException;

import java.util.*;

/**
 * @author MiguelAraCo
 * @since _version_
 */
public class InMemoryGraphQueryResult implements GraphQueryResult {
	Set<Statement> statements;
	Iterator<Statement> statementIterator;
	Map<String, String> namespaces;

	private InMemoryGraphQueryResult() {
		statements = new HashSet<>();
		namespaces = new HashMap<>();
	}

	public static InMemoryGraphQueryResult from( GraphQueryResult queryResult ) throws QueryEvaluationException {
		InMemoryGraphQueryResult memoryGraphQueryResult = new InMemoryGraphQueryResult();

		while ( queryResult.hasNext() ) {
			memoryGraphQueryResult.statements.add( queryResult.next() );
		}

		memoryGraphQueryResult.namespaces.putAll( queryResult.getNamespaces() );

		return memoryGraphQueryResult;
	}

	@Override
	public Map<String, String> getNamespaces() throws QueryEvaluationException {
		return ImmutableMap.copyOf( this.namespaces );
	}

	@Override
	public void close() throws QueryEvaluationException {
		this.statementIterator = null;
	}

	@Override
	public boolean hasNext() throws QueryEvaluationException {
		if ( statementIterator == null ) this.statementIterator = statements.iterator();
		return statementIterator.hasNext();
	}

	@Override
	public Statement next() throws QueryEvaluationException {
		if ( statementIterator == null ) this.statementIterator = statements.iterator();
		return statementIterator.next();
	}

	@Override
	public void remove() throws QueryEvaluationException {
		throw new UnsupportedOperationException();
	}
}
