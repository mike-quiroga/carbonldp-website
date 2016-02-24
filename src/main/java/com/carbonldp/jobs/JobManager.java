package com.carbonldp.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@Transactional
public class JobManager {

	public JobsExecutor jobsExecutor;

	@Scheduled( cron = "${job.trigger.time}" )
	public void doSomething() {
		System.out.println( "imprimo" );
		jobsExecutor.specialMethod();
		jobsExecutor.specialMethod2();
	}

	@Autowired
	public void setJobsExecutor( JobsExecutor jobsExecutor ) { this.jobsExecutor = jobsExecutor; }
}
