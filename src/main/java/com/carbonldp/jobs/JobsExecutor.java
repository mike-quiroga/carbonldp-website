package com.carbonldp.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class JobsExecutor {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	private final List<TypedJobExecutor> typedJobs;

	public JobsExecutor( List<TypedJobExecutor> typedJobs ) {
		this.typedJobs = typedJobs;
	}

	@Async
	public void runJob( Job job ) {
		JobDescription.Type type = BackupJobFactory.getInstance().getJobType( job );

		getTypedRepository( type ).run( job );
	}

	public TypedJobExecutor getTypedRepository( JobDescription.Type jobType ) {
		for ( TypedJobExecutor job : typedJobs ) {
			if ( job.supports( jobType ) ) return job;
		}
		throw new IllegalArgumentException( "The jobType provided isn't supported" );
	}
}
