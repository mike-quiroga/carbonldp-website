package com.carbonldp.apps;

import com.carbonldp.jobs.Job;
import org.openrdf.model.URI;

public interface AppRepository {
	public boolean exists( URI appURI );

	public App get( URI appURI );

	public App findByRootContainer( URI rootContainerURI );

	public App createPlatformAppRepository( App app );

	public void delete( URI appURI );

	public URI getPlatformAppContainerURI();

	public Job peekJobsQueue( App app );
}
