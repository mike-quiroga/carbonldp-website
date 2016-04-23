package com.carbonldp.jobs;

import com.carbonldp.apps.App;

/**
 * @author NestorVenegas
 * @since 0.33.0
 */

public interface TypedJobExecutor {

	public boolean supports( JobDescription.Type jobType );

	public void execute( App app, Job job, Execution execution );
}
