package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import com.carbonldp.agents.Agent;
import com.carbonldp.agents.PlatformAgentDescription;
import com.carbonldp.agents.platform.SesamePlatformAgentService;
import com.carbonldp.apps.App;
import org.openrdf.model.IRI;
import org.springframework.aop.framework.Advised;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since 0.37.0
 */
public class UpdateAction1o10o0 extends AbstractUpdateAction {

	@Override
	protected void execute() throws Exception {
		createRDFMaps();
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> {
				IRI appRoleContainerIRI = appRoleRepository.getContainerIRI();
				IRI appRoleAgentContainerIRI = valueFactory.createIRI( appRoleContainerIRI.stringValue() + Vars.getInstance().getAppRoleAgentsContainer() );
				IRI membershipResource = containerRepository.getTypedRepository( containerService.getContainerType( appRoleAgentContainerIRI ) ).getMembershipResource( appRoleAgentContainerIRI );
			} );
		}
	}

	private void createRDFMaps() throws Exception {
		Set<IRI> platformAgentIRIs = containerRepository.getMemberIRIs( platformAgentRepository.getAgentsContainerIRI() );
		SesamePlatformAgentService sesamePlatformAgentService = (SesamePlatformAgentService) ( (Advised) platformAgentService ).getTargetSource().getTarget();
		transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> {
			for ( IRI platformAgentIRI : platformAgentIRIs ) {
				Agent agentResource = platformAgentRepository.get( platformAgentIRI );
				if ( agentResource.getBNode( PlatformAgentDescription.Property.APP_ROLE_MAP ) == null && ! platformAgentIRI.stringValue().equals( platformAgentRepository.getAgentsContainerIRI().stringValue() + "admin/" ) ) {
					sesamePlatformAgentService.createAppRoleMap( agentResource );
					sourceService.replace( agentResource );
				}
			}
		} );
	}
}
