package com.carbonldp.repository.updates;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.PlatformAgentDescription;
import org.openrdf.model.IRI;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since 0.37.0
 */
public class UpdateAction1o10o0 extends AbstractUpdateAction {

	@Override
	protected void execute() throws Exception {
		createRDFMaps();
//		Set<App> apps = getAllApps();
//		for ( App app : apps ) {
//			transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> {
//				if ( sourceRepository.exists( IRIUtil.createChildIRI( app.getRootContainerIRI(), Vars.getInstance().getAppTicketsContainer() ) ) ) return;
//				appTokensRepository.createTicketsContainer( app.getRootContainerIRI() );
//			} );
//		}
	}

	private void createRDFMaps() {
		Set<IRI> platformAgentIRIs = containerRepository.getMemberIRIs( platformAgentRepository.getAgentsContainerIRI() );
		for ( IRI platformAgentIRI : platformAgentIRIs ) {
			Agent agentResource = platformAgentRepository.get( platformAgentIRI );
			if ( agentResource.getBNode( PlatformAgentDescription.Property.APP_ROLE_MAP ) == null && ! platformAgentIRI.stringValue().equals( platformAgentRepository.getAgentsContainerIRI().stringValue() + "admin/" ) ) {
				platformAgentService.createAppRoleMap( agentResource );
			}
		}
	}
}
