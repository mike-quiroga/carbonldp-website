package com.carbonldp.sparql;

import org.openrdf.model.URI;

public interface SPARQLService {
	public SPARQLResult executeSPARQLQuery( String queryString, URI targetURI );
}
