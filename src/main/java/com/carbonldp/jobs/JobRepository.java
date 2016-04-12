package com.carbonldp.jobs;

import org.openrdf.model.IRI;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface JobRepository {

	public IRI getExecutionQueueLocation(IRI job);
}
