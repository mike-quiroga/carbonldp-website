package com.carbonldp.authentication;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.apps.App;
import com.carbonldp.authorization.*;
import org.eclipse.rdf4j.model.IRI;

import java.util.Set;

public abstract class AbstractSesameAuthenticationProvider extends AbstractAuthenticationProvider {
	protected final AgentRepository agentRepository;

	protected final PlatformRoleRepository platformRoleRepository;
	protected final PlatformPrivilegeRepository platformPrivilegeRepository;

	public AbstractSesameAuthenticationProvider( AgentRepository agentRepository, PlatformRoleRepository platformRoleRepository, PlatformPrivilegeRepository platformPrivilegeRepository ) {
		this.agentRepository = agentRepository;
		this.platformRoleRepository = platformRoleRepository;
		this.platformPrivilegeRepository = platformPrivilegeRepository;
	}

	protected AgentAuthenticationToken createAgentAuthenticationToken( App app, Agent agent ) {
		Set<PlatformRole> platformRoles = platformRoleRepository.get( agent );
		Set<PlatformPrivilege> platformPrivileges = platformPrivilegeRepository.get( platformRoles );

		Set<Platform.Role> platformRoleRepresentations = platformRoleRepository.getRepresentations( platformRoles );
		Set<Platform.Privilege> platformPrivilegeRepresentations = platformPrivilegeRepository.getRepresentations( platformPrivileges );

		AgentAuthenticationToken token = app == null ?
			new AgentAuthenticationToken( agent, platformRoleRepresentations, platformPrivilegeRepresentations ) :
			new AgentAuthenticationToken( app, agent, platformRoleRepresentations, platformPrivilegeRepresentations );
		token.eraseCredentials();
		token.setAuthenticated( true );
		return token;
	}

	protected AgentAuthenticationToken createAgentAuthenticationToken( Agent agent ) {
		return createAgentAuthenticationToken( null, agent );
	}
}
