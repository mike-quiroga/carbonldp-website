package com.carbonldp.apps;

import com.carbonldp.jobs.Execution;
import org.openrdf.model.URI;

public interface AppRepository {
	public boolean exists( URI appURI );

	public App get( URI appURI );

	public App findByRootContainer( URI rootContainerURI );

	public App createPlatformAppRepository( App app );

	public void deleteAppRepository( URI appURI );

	public URI getPlatformAppContainerURI();

	public Execution peekJobsExecutionQueue( App app );

	public void dequeueJobsExecutionQueue( App app );
}
