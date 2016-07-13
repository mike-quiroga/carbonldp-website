package com.carbonldp.ldp.patch;

import org.eclipse.rdf4j.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PATCHRequestService {

	@PreAuthorize( "hasPermission(#sourceIRI, 'UPDATE')" )
	void execute( IRI sourceIRI, PATCHRequest patchRequest );
}
