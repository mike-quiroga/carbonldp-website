package com.carbonldp.sparql;

import org.eclipse.rdf4j.query.TupleQueryResult;

public class SPARQLTupleResult implements SPARQLResult<TupleQueryResult> {
	private TupleQueryResult result;

	public SPARQLTupleResult( TupleQueryResult result ) {
		this.result = result;
	}

	@Override
	public TupleQueryResult getResult() {
		return result;
	}
}
