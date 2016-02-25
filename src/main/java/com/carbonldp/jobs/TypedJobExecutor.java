package com.carbonldp.jobs;

import com.carbonldp.ldp.containers.ContainerDescription;

/**
 * @author NestorVenegas
 * @since _version_
 */

public interface TypedJobExecutor {

	public boolean supports( JobDescription.Type jobType );

	public void run( Job job );
}
