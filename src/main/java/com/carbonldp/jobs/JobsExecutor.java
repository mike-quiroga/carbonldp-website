package com.carbonldp.jobs;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.openrdf.model.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class JobsExecutor {
	protected final Logger LOG = LoggerFactory.getLogger( this.getClass() );
	private final List<TypedJobExecutor> typedJobs;
	private AppRepository appRepository;
	private JobService jobService;
	private TransactionWrapper transactionWrapper;

	public JobsExecutor( List<TypedJobExecutor> typedJobs ) {
		this.typedJobs = typedJobs;
	}

	@Async
	public void execute( Execution execution ) {
		Job job = transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> jobService.get( execution.getJobURI() ) );
		JobDescription.Type type = JobFactory.getInstance().getJobType( job );

		getTypedRepository( type ).execute( job, execution );
		dequeueJobsExecutionQueue( job.getAppRelated() );
	}

	public TypedJobExecutor getTypedRepository( JobDescription.Type jobType ) {
		for ( TypedJobExecutor job : typedJobs ) {
			if ( job.supports( jobType ) ) return job;
		}
		throw new IllegalArgumentException( "The jobType provided isn't supported" );
	}

	private void dequeueJobsExecutionQueue( URI appURI ) {
		App app = appRepository.get( appURI );
		appRepository.dequeueJobsExecutionQueue( app );
	}

	@Autowired
	public void setAppRepository( AppRepository appRepository ) {this.appRepository = appRepository; }

	@Autowired
	public void setJobService( JobService jobService ) {this.jobService = jobService;}

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) { this.transactionWrapper = transactionWrapper; }
}
