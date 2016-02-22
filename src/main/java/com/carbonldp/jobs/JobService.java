package com.carbonldp.jobs;

import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public interface JobService {

	@PreAuthorize( "hasPermission(#targetURI, 'CREATE_CHILD')" )
	public void create( URI targetURI, Job job );
}
