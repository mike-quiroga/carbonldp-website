package com.carbonldp.jobs;

import org.openrdf.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author JorgeEspinosa
 * @since 0.33.0
 */
public interface JobService {

	@PreAuthorize( "hasPermission(#targetIRI, 'CREATE_CHILD')" )
	public void create( IRI targetIRI, Job job );

	@PreAuthorize( "hasPermission(#targetIRI, 'READ')" )
	public Job get( IRI targetIRI );

	@PreAuthorize( "hasPermission(#jobIRI, 'CREATE_CHILD')" )
	public void createExecution( IRI jobIRI, Execution execution );
}
