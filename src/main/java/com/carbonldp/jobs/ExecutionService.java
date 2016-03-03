package com.carbonldp.jobs;

import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface ExecutionService {

	@PreAuthorize( "hasPermission(#executionsContainerURI, 'CREATE_CHILD')" )
	public void createChild( URI executionsContainerURI, Execution execution );
}
