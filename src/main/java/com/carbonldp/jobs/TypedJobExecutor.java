package com.carbonldp.jobs;

/**
 * @author NestorVenegas
 * @since _version_
 */

public interface TypedJobExecutor {

	public boolean supports( JobDescription.Type jobType );

	public void run( Job job );
}
