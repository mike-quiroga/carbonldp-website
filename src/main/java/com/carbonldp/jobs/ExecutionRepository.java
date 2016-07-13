package com.carbonldp.jobs;

import org.eclipse.rdf4j.model.BNode;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Value;

/**
 * @author NestorVenegas
 * @since 0.33.0
 */
public interface ExecutionRepository {

	public void changeExecutionStatus( IRI executionIRI, ExecutionDescription.Status status );

	public void enqueue( BNode bNode, IRI executionQueueLocationIRI );

	public void dequeue( IRI executionQueueLocationIRI );

	public Execution peek( IRI executionQueueLocationIRI );

	public void addResult( IRI executionIRI, Value status );

	public void addErrorDescription( IRI executionIRI, String error );
}
