package com.carbonldp.ldp.patch;

import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PATCHRequestService {

	@PreAuthorize( "hasPermission(#sourceURI, 'UPDATE')" )
	void execute( URI sourceURI, PATCHRequest patchRequest );
}
