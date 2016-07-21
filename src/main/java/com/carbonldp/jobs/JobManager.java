package com.carbonldp.jobs;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since 0.33.0
 */
public class JobManager {

	private JobsExecutor jobsExecutor;
	private TransactionWrapper transactionWrapper;
	private AppRepository appRepository;
	private ExecutionService executionService;
	ValueFactory valueFactory = SimpleValueFactory.getInstance();

	@Scheduled( cron = "${job.execution.time}" )
	public void runQueuedJobs() {
		lookUpForJobs();
	}

	public void lookUpForJobs() {
		Set<App> apps = appRepository.getAll();
		for ( App app : apps ) {
			IRI jobsContainerIRI = valueFactory.createIRI( app.getIRI().toString().concat( Vars.getInstance().getJobsContainer() ) );
			Execution execution = transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> executionService.peek( jobsContainerIRI ) );
			if ( execution != null && ( ! execution.getStatus().equals( ExecutionDescription.Status.RUNNING.getIRI() ) ) ) {
				jobsExecutor.execute( app, execution );
			}
		}
	}

	@Autowired
	public void setJobsExecutor( JobsExecutor jobsExecutor ) { this.jobsExecutor = jobsExecutor; }

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) {this.transactionWrapper = transactionWrapper; }

	@Autowired
	public void setAppRepository( AppRepository appRepository ) {this.appRepository = appRepository; }

	@Autowired
	public void setExecutionService( ExecutionService executionService ) {
		this.executionService = executionService;
	}
}
