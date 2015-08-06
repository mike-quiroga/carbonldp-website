package com.carbonldp.sparql;

public class SPARQLBooleanResult implements SPARQLResult<Boolean> {
	private boolean result;

	public SPARQLBooleanResult( boolean result ) {
		this.result = result;
	}

	@Override
	public Boolean getResult() {
		return result;
	}
}
