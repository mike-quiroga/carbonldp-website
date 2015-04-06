package com.carbonldp.ldp.patch;

import org.openrdf.model.URI;

public interface PATCHRequestService {
	void execute( URI sourceURI, PATCHRequest patchRequest );
}
