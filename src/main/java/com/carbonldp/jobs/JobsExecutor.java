package com.carbonldp.jobs;

import com.carbonldp.apps.App;
import com.carbonldp.apps.context.RunInPlatformContext;
import com.carbonldp.authorization.Platform;
import com.carbonldp.authorization.RunWith;
import com.carbonldp.spring.TransactionWrapper;
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
	private final List<TypedJobExecutor> typedJobs;
	private JobService jobService;
	private ExecutionService executionService;
	private TransactionWrapper transactionWrapper;

	public JobsExecutor( List<TypedJobExecutor> typedJobs ) {
		this.typedJobs = typedJobs;
	}

	@Async
	public void execute( App app, Execution execution ) {
		transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> manageExecution( app, execution ) );
	}

	private void manageExecution( App app, Execution execution ) {

		executionService.changeExecutionStatus( execution.getIRI(), ExecutionDescription.Status.RUNNING );
		Job job = jobService.get( execution.getJobIRI() );
		JobDescription.Type type = JobFactory.getInstance().getJobType( job );
		boolean hasErrors = false;

		try {
			getTypedRepository( type ).execute( app, job, execution );
		} catch ( Exception e ) {
			executionService.changeExecutionStatus( execution.getIRI(), ExecutionDescription.Status.ERROR );
			hasErrors = true;
		}
		if ( ! hasErrors ) executionService.changeExecutionStatus( execution.getIRI(), ExecutionDescription.Status.FINISHED );
		executionService.dequeue( job.getIRI( JobDescription.Property.EXECUTION_QUEUE_LOCATION.getIRI() ) );
	}

	private TypedJobExecutor getTypedRepository( JobDescription.Type jobType ) {
		for ( TypedJobExecutor job : typedJobs ) {
			if ( job.supports( jobType ) ) return job;
		}
		throw new IllegalArgumentException( "The jobType provided isn't supported" );
	}

	@Autowired
	public void setJobService( JobService jobService ) {this.jobService = jobService;}

	@Autowired
	public void setExecutionService( ExecutionService executionService ) { this.executionService = executionService; }

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) {
		this.transactionWrapper = transactionWrapper;
	}
}
