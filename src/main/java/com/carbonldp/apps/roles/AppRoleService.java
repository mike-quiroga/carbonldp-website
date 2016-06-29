package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRole;
import org.openrdf.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Collection;
import java.util.Set;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since 0.18.0-ALPHA
 */
public interface AppRoleService {

	public boolean exists( IRI appIRI );

	public void create( AppRole appRole );

	public void addChildren( IRI parentRole, Set<IRI> children );

	public void addAgents( IRI appRoleAgentContainerIRI, Collection<IRI> agents );

	public void addAgent( IRI appRoleAgentConatinerIRI, IRI agent );

	public void removeAgents( IRI appRoleAgentContainerIRI );

	public void removeAgents( IRI appRoleAgentContainerIRI, Collection<IRI> agents );

	public void removeAgent( IRI appRoleAgentConatinerIRI, IRI agent );

	public void addChild( IRI parentRole, IRI child );

	@PreAuthorize( "hasPermission(#appRoleIRI, 'DELETE')" )
	public void delete( IRI appRoleIRI );

	public IRI getAgentsContainerIRI( IRI appRoleIRI );
}
