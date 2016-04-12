package com.carbonldp.jobs;

import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface ExecutionRepository {
	public ExecutionDescription.Status getExecutionStatus( IRI executionIRI );

	public void changeExecutionStatus( IRI executionIRI, ExecutionDescription.Status status );

	public void enqueue( BNode bNode, IRI executionQueueLocationIRI );

	public void dequeue( IRI executionQueueLocationIRI );

	public Execution peek( IRI executionQueueLocationIRI );

	public void addResult(IRI executionIRI, Value status);

	public void addErrorDescription(IRI executionIRI, String error);
}
