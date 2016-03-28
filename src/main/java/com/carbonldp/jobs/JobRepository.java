package com.carbonldp.jobs;

import org.openrdf.model.URI;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface JobRepository {

	public URI getExecutionQueueLocation(URI job);
}
