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
	private JobService jobService;
	private TransactionWrapper transactionWrapper;
	private ExecutionRepository executionRepository;

	public JobsExecutor( List<TypedJobExecutor> typedJobs ) {
		this.typedJobs = typedJobs;
	}

	@Async
	public void execute( App app, Execution execution ) {

		executionRepository.changeExecutionStatus( execution.getURI(), ExecutionDescription.Status.RUNNING );
		Job job = transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> jobService.get( execution.getJobURI() ) );
		JobDescription.Type type = JobFactory.getInstance().getJobType( job );
		boolean hasErrors = false;

		try {
			transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> getTypedRepository( type ).execute( app, job, execution ) );
		} catch ( Exception e ) {
			executionRepository.changeExecutionStatus( execution.getURI(), ExecutionDescription.Status.ERROR );
			hasErrors = true;
		}
		if ( ! hasErrors ) executionRepository.changeExecutionStatus( execution.getURI(), ExecutionDescription.Status.FINISHED );
		executionRepository.dequeue( job.getURI( JobDescription.Property.EXECUTION_QUEUE_LOCATION.getURI() ) );
	}

	public TypedJobExecutor getTypedRepository( JobDescription.Type jobType ) {
		for ( TypedJobExecutor job : typedJobs ) {
			if ( job.supports( jobType ) ) return job;
		}
		throw new IllegalArgumentException( "The jobType provided isn't supported" );
	}

	@Autowired
	public void setJobService( JobService jobService ) {this.jobService = jobService;}

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) { this.transactionWrapper = transactionWrapper; }

	@Autowired
	public void setExecutionRepository( ExecutionRepository executionRepository ) { this.executionRepository = executionRepository; }
}
