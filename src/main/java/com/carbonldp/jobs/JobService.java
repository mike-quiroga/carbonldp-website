package com.carbonldp.jobs;

import org.openrdf.model.URI;
import org.springframework.security.access.prepost.PreAuthorize;
import com.carbonldp.jobs.JobDescription.JobStatus;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public interface JobService {

	@PreAuthorize( "hasPermission(#targetURI, 'CREATE_CHILD')" )
	public void create( URI targetURI, Job job );

	@PreAuthorize( "hasPermission(#targetURI, 'READ')" )
	public Job get( URI jobURI );

	@PreAuthorize( "hasPermission(#targetURI, 'UPDATE')" )
	public void changeJobStatus( URI jobURI, JobStatus jobStatus );
}
