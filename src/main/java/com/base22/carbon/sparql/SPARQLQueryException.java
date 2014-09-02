package com.base22.carbon.sparql;

import com.base22.carbon.CarbonException;

public class SPARQLQueryException extends CarbonException {
	private static final long serialVersionUID = 2304595096946745341L;

	public SPARQLQueryException(SPARQLQuery sparqlQuery, String message) {
		super(message);
	}
}
