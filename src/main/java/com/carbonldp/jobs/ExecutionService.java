package com.carbonldp.jobs;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author NestorVenegas
 * @since _version_
 */
public interface ExecutionService {

	@PreAuthorize( "hasPermission(#executionURI, 'READ')" )
	public ExecutionDescription.Status getExecutionStatus( URI executionURI );

	@PreAuthorize( "hasPermission(#executionURI, 'UPDATE')" )
	public void changeExecutionStatus( URI executionURI, ExecutionDescription.Status status );

	@PreAuthorize( "hasPermission(#executionQueueLocationURI, 'UPDATE')" )
	public void enqueue( URI executionURI, URI executionQueueLocationURI );

	@PreAuthorize( "hasPermission(#executionQueueLocationURI, 'UPDATE')" )
	public void dequeue( URI executionQueueLocationURI );

	@PreAuthorize( "hasPermission(#executionQueueLocationURI, 'READ')" )
	public Execution peek( URI executionQueueLocationURI );

	@PreAuthorize( "hasPermission(#executionURI, 'UPDATE')" )
	public void addResult( URI executionURI, Value status );
}
