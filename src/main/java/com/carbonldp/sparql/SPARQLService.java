package com.carbonldp.sparql;

import org.openrdf.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

public interface SPARQLService {

	@PreAuthorize( "hasPermission(#targetIRI, 'READ')" )
	public SPARQLResult executeSPARQLQuery( String queryString, IRI targetIRI );

	@PreAuthorize( "hasPermission(#targetIRI, 'UPDATE')" )
	public void executeSPARQLUpdate( String sparqlUpdate, IRI targetIRI );
}
