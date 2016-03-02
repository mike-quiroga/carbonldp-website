package com.carbonldp.jobs;

import org.openrdf.model.URI;
import com.carbonldp.jobs.JobDescription.JobStatus;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface JobRepository {

	public JobStatus getJobStatus( URI jobURI );

	public void changeJobStatus( URI jobURI, JobStatus jobStatus );

	public URI getAppURI( URI jobURI );
}
