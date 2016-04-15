package com.carbonldp.jobs;

import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface ExecutionService {

	@PreAuthorize( "hasPermission(#executionIRI, 'UPDATE')" )
	public void changeExecutionStatus( IRI executionIRI, ExecutionDescription.Status status );

	@PreAuthorize( "hasPermission(#executionQueueLocationIRI, 'UPDATE')" )
	public void enqueue( IRI executionIRI, IRI executionQueueLocationIRI );

	@PreAuthorize( "hasPermission(#executionQueueLocationIRI, 'UPDATE')" )
	public void dequeue( IRI executionQueueLocationIRI );

	@PreAuthorize( "hasPermission(#executionQueueLocationIRI, 'READ')" )
	public Execution peek( IRI executionQueueLocationIRI );

	@PreAuthorize( "hasPermission(#executionIRI, 'UPDATE')" )
	public void addResult( IRI executionIRI, Value status );
}
