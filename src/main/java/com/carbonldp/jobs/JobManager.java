package com.carbonldp.jobs;

import com.carbonldp.apps.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@Transactional
public class JobManager {

	public JobsExecutor jobsExecutor;

	@Scheduled( cron = "${job.trigger.time}" )
	public void doSomething() {
		lookUpForBackups();
	}

	public void lookUpForBackups() {
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			if ( needBackup( app ) ) {
				jobsExecutor.runBackup( app );
			}
		}
	}

	private Set<App> getAllApps() {
		//TODO: implement
		return null;
	}

	public boolean needBackup( App app ) {
		//TODO: implement
		return false;
	}

	@Autowired
	public void setJobsExecutor( JobsExecutor jobsExecutor ) { this.jobsExecutor = jobsExecutor; }
}
