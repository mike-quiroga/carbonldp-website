package com.carbonldp.sparql;

import org.eclipse.rdf4j.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

public interface SPARQLService {

	@PreAuthorize( "hasPermission(#targetIRI, 'READ')" )
	public SPARQLResult executeSPARQLQuery( String queryString, IRI targetIRI );

	@PreAuthorize( "hasRole('SYSTEM')" )
	public void executeSPARQLUpdate( String sparqlUpdate, IRI targetIRI );
}
