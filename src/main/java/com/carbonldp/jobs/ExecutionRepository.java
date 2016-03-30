package com.carbonldp.jobs;

import org.openrdf.model.BNode;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface ExecutionRepository {
	public ExecutionDescription.Status getExecutionStatus( URI executionURI );

	public void changeExecutionStatus( URI executionURI, ExecutionDescription.Status status );

	public void enqueue( BNode bNode, URI executionQueueLocationURI );

	public void dequeue( URI executionQueueLocationURI );

	public Execution peek( URI executionQueueLocationURI );

	public void addResult( URI executionURI, Value status );
}
