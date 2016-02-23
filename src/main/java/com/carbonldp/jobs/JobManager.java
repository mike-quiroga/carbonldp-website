package com.carbonldp.jobs;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
@Transactional
public class JobManager {

	@Scheduled( cron = "${job.trigger.time}" )
	public void doSomething() {
		System.out.println( "imprimo" );
	}
}
