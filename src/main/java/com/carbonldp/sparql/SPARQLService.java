package com.carbonldp.sparql;

import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

public interface SPARQLService {

	@PreAuthorize( "hasPermission(#targetURI, 'READ')" )
	public SPARQLResult executeSPARQLQuery( String queryString, URI targetURI );
}
