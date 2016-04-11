package com.carbonldp.apps.roles;

import com.carbonldp.apps.AppRole;
import org.openrdf.model.IRI;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since 0.18.0-ALPHA
 */
public interface AppRoleService {

	public boolean exists( IRI appIRI );

	public void addAgents( IRI appRoleAgentContainerIRI, Set<IRI> agents );

	public void create( AppRole appRole );

	public void addChildren( IRI parentRole, Set<IRI> childs );

	public void addAgent( IRI appRoleAgentConatinerIRI, IRI agent );

	public void addChild( IRI parentRole, IRI child );

	@PreAuthorize( "hasPermission(#appRoleIRI, 'DELETE')" )
	public void delete( IRI appRoleIRI );
}
