package com.carbonldp.jobs;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
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
	private ExecutionService executionService;
	ValueFactory valueFactory = SimpleValueFactory.getInstance();

	@Scheduled( cron = "${job.execution.time}" )
	public void runQueuedJobs() {
		lookUpForJobs();
	}

	public void lookUpForJobs() {
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			IRI jobsContainerIRI = valueFactory.createIRI( app.getIRI().toString().concat( Vars.getInstance().getJobsContainer() ) );
			Execution execution = transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> executionService.peek( jobsContainerIRI ) );
			if ( execution != null && ( ! execution.getStatus().equals( ExecutionDescription.Status.RUNNING.getIRI() ) ) ) {
				jobsExecutor.execute( app, execution );
			}
		}
	}

	private Set<App> getAllApps() {
		return transactionWrapper.runInPlatformContext( () -> {
			Set<App> apps = new HashSet<>();
			IRI platformAppsContainer = valueFactory.createIRI( Vars.getInstance().getHost() + Vars.getInstance().getMainContainer() + Vars.getInstance().getAppsContainer() );
			Set<IRI> appIRIs = containerRepository.getContainedIRIs( platformAppsContainer );
			for ( IRI appIRI : appIRIs ) {
				App app = appRepository.get( appIRI );
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
	public void setExecutionService( ExecutionService executionService ) {
		this.executionService = executionService;
	}
}
