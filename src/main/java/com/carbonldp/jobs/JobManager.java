package com.carbonldp.jobs;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.HashSet;
import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class JobManager {

	private JobsExecutor jobsExecutor;
	private TransactionWrapper transactionWrapper;
	private ContainerRepository containerRepository;
	private AppRepository appRepository;
	private ExecutionRepository executionRepository;

	@Scheduled( cron = "${job.execution.time}" )
	public void runQueuedJobs() {
		lookUpForJobs();
	}

	public void lookUpForJobs() {
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			URI jobsContainerURI = new URIImpl( app.getURI().toString().concat( Vars.getInstance().getJobsContainer() ) );
			Execution execution = executionRepository.peek( jobsContainerURI );
			if ( execution != null && ( ! execution.getStatus().equals( ExecutionDescription.Status.RUNNING.getURI() ) ) ) {
				jobsExecutor.execute( app, execution );
			}
		}
	}

	private Set<App> getAllApps() {
		return transactionWrapper.runInPlatformContext( () -> {
			Set<App> apps = new HashSet<>();
			URI platformAppsContainer = new URIImpl( Vars.getInstance().getHost() + Vars.getInstance().getMainContainer() + Vars.getInstance().getAppsContainer() );
			Set<URI> appURIs = containerRepository.getContainedURIs( platformAppsContainer );
			for ( URI appURI : appURIs ) {
				App app = appRepository.get( appURI );
				apps.add( app );
			}
			return apps;
		} );
	}

	@Autowired
	public void setJobsExecutor( JobsExecutor jobsExecutor ) { this.jobsExecutor = jobsExecutor; }

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) {this.transactionWrapper = transactionWrapper; }

	@Autowired
	public void setContainerRepository( ContainerRepository containerRepository ) { this.containerRepository = containerRepository;}

	@Autowired
	public void setAppRepository( AppRepository appRepository ) {this.appRepository = appRepository; }

	@Autowired
	public void setExecutionRepository( ExecutionRepository executionRepository ) {
		this.executionRepository = executionRepository;
	}
}
