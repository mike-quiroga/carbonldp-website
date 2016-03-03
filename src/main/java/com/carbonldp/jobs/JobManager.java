package com.carbonldp.jobs;

import com.carbonldp.apps.AppRepository;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class JobManager {

	private JobsExecutor jobsExecutor;
	private TransactionWrapper transactionWrapper;
	private ContainerRepository containerRepository;
	private AppRepository appRepository;

	/*
	@Scheduled( cron = "${job.trigger.time}" )
	public void runQueuedJobs() {
		lookUpForJobs();
	}

	public void lookUpForJobs() {
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			Job job = appRepository.peekJobsQueue( app );
			if ( job != null && ( ! job.getJobStatus().equals( JobDescription.JobStatus.RUNNING.getURI() ) ) ) {
				jobsExecutor.runJob( job );
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
	*/

	@Autowired
	public void setJobsExecutor( JobsExecutor jobsExecutor ) { this.jobsExecutor = jobsExecutor; }

	@Autowired
	public void setTransactionWrapper( TransactionWrapper transactionWrapper ) {this.transactionWrapper = transactionWrapper; }

	@Autowired
	public void setContainerRepository( ContainerRepository containerRepository ) { this.containerRepository = containerRepository;}

	@Autowired
	public void setAppRepository( AppRepository appRepository ) {this.appRepository = appRepository; }
}
