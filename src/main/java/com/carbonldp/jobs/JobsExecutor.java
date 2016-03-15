package com.carbonldp.jobs;

import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.exceptions.JobException;
import com.carbonldp.models.Infraction;
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
	private ExecutionRepository executionRepository;

	public JobsExecutor( List<TypedJobExecutor> typedJobs ) {
		this.typedJobs = typedJobs;
	}

	@Async
	public void execute( Execution execution ) {
		LOG.debug( "Running execution " + Thread.currentThread().getName(), Thread.currentThread().getName() );

		executionRepository.changeExecutionStatus( execution.getURI(), ExecutionDescription.Status.RUNNING );
		Job job = transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> jobService.get( execution.getJobURI() ) );
		JobDescription.Type type = JobFactory.getInstance().getJobType( job );
		boolean hasErrors = false;

		LOG.debug( "Running execution " + Thread.currentThread().getName() + " Job " + job.getSubject(), Thread.currentThread().getName() );

		try {
			getTypedRepository( type ).execute( job, execution );
		} catch ( JobException e ) {
			executionRepository.changeExecutionStatus( execution.getURI(), ExecutionDescription.Status.ERROR );

			List<Infraction> infractions = e.getInfractions();
			for ( Infraction infraction : infractions ) {
				executionRepository.addErrorDescription( execution.getURI(), infraction.getErrorMessage() );
			}

			hasErrors = true;
		} catch ( Exception e ) {
			executionRepository.changeExecutionStatus( execution.getURI(), ExecutionDescription.Status.UNKNOWN );
			hasErrors = true;
		}
		if ( ! hasErrors ) executionRepository.changeExecutionStatus( execution.getURI(), ExecutionDescription.Status.FINISHED );
		dequeueJobsExecutionQueue( job.getAppRelated() );

		LOG.debug( "Ending execution " + Thread.currentThread().getName(), Thread.currentThread().getName() );
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

	@Autowired
	public void setExecutionRepository( ExecutionRepository executionRepository ) { this.executionRepository = executionRepository; }
}
