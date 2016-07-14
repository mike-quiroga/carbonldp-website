package com.carbonldp.sparql;

import org.eclipse.rdf4j.query.GraphQueryResult;

public class SPARQLGraphResult implements SPARQLResult<GraphQueryResult> {
	private GraphQueryResult result;

	public SPARQLGraphResult( GraphQueryResult result ) {
		this.result = result;
	}

	@Override
	public GraphQueryResult getResult() {
		return result;
	}
}
