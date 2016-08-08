package com.carbonldp.agents;

import org.eclipse.rdf4j.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * @author NestorVenegas
 * @since 0.14.0-ALPHA
 */
public interface AgentService {
	@PreAuthorize( " ! isAuthenticated() or hasRole('ROLE_ANONYMOUS')" )
	public void register( Agent agent );

	@PreAuthorize( "hasPermission(#agentIRI, 'DELETE')" )
	public void delete( IRI agentIRI );

	@PreAuthorize( "hasPermission(#agentContainerIRI, 'CREATE_CHILD')" )
	public void create( IRI agentContainerIRI, Agent agent );

	@PreAuthorize( "hasPermission(#source, 'UPDATE')" )
	public void replace( IRI source, Agent agent );

	@PreAuthorize( "hasPermission(#agentIRI, 'READ')" )
	public Agent get( IRI agentIRI );
}
