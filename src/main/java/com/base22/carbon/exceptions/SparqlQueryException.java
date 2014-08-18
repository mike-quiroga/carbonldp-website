package com.base22.carbon.exceptions;

import com.base22.carbon.models.SparqlQuery;

public class SparqlQueryException extends CarbonException {
	private static final long serialVersionUID = 2304595096946745341L;

	public SparqlQueryException(SparqlQuery sparqlQuery, String message) {
		super(message);
	}
}
