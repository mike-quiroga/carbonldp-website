package com.carbonldp.repository.updates;

import com.carbonldp.agents.Agent;
import com.carbonldp.apps.App;
import com.carbonldp.authorization.acl.ACEDescription;
import com.carbonldp.authorization.acl.ACL;
import org.eclipse.rdf4j.model.IRI;

import java.util.Arrays;
import java.util.Set;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class UpdateAction1o17o0 extends AbstractUpdateAction {

	@Override
	protected void execute() throws Exception {
		IRI platformAgentsContainerIRI = platformAgentRepository.getAgentsContainerIRI();
		addDefaultPermissionsToAgents( platformAgentsContainerIRI );
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> {
				IRI appAgentsContainerIRI = appAgentRepository.getAgentsContainerIRI();
				addDefaultPermissionsToAgents( appAgentsContainerIRI );
			} );

		}
	}

	private void addDefaultPermissionsToAgents( IRI agentsContainerIRI ) {
		Set<IRI> platformAgentsIRI = containerRepository.getMemberIRIs( agentsContainerIRI );
		for ( IRI platformAgentIRI : platformAgentsIRI ) {
			Agent platformAgent = new Agent( sourceRepository.get( platformAgentIRI ) );
			addAgentDefaultPermissions( platformAgent );
		}
	}

	private void addAgentDefaultPermissions( Agent agent ) {
		ACL agentACL = aclRepository.getResourceACL( agent.getIRI() );
		aclRepository.grantPermissions( agentACL, Arrays.asList( agent ), Arrays.asList(
			ACEDescription.Permission.READ,
			ACEDescription.Permission.UPDATE,
			ACEDescription.Permission.DELETE
		), false );
	}
}
