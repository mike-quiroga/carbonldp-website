package com.carbonldp.repository.updates;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.PlatformAgentDescription;
import com.carbonldp.agents.platform.SesamePlatformAgentService;
import com.carbonldp.apps.App;
import com.carbonldp.utils.ValueUtil;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.springframework.aop.framework.Advised;

import java.util.Set;

/**
 * add app role map to platform agents
 * @author JorgeEspinosa
 * @since _version_
 */
public class UpdateAction1o15o0 extends AbstractUpdateAction {

	@Override
	protected void execute() throws Exception {
		createAppRoleMap();
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> addRolesToAppRoleMaps( app ) );
		}
	}

	private void createAppRoleMap() throws Exception {
		Set<IRI> platformAgentIRIs = containerRepository.getMemberIRIs( platformAgentRepository.getAgentsContainerIRI() );
		SesamePlatformAgentService sesamePlatformAgentService = (SesamePlatformAgentService) ( (Advised) platformAgentService ).getTargetSource().getTarget();
		transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> {
			for ( IRI platformAgentIRI : platformAgentIRIs ) {
				Agent agentResource = platformAgentRepository.get( platformAgentIRI );
				if ( agentResource.getIRI( PlatformAgentDescription.Property.APP_ROLE_MAP ) == null ) {
					sesamePlatformAgentService.createAppRoleMap( agentResource );
				}
			}
		} );
	}

	private void addRolesToAppRoleMaps( App app ) {
		IRI appRoleContainerIRI = appRoleRepository.getContainerIRI();
		Set<Statement> members = containerService.getMembershipTriples( appRoleContainerIRI );
		for ( Statement member : members ) {
			IRI roleIRI = ValueUtil.getIRI( member.getObject() );
			Set<Statement> roleMembers = containerService.getMembershipTriples( valueFactory.createIRI( roleIRI.stringValue() + "agents/" ) );
			for ( Statement roleMember : roleMembers ) {
				IRI roleMemberIRI = ValueUtil.getIRI( roleMember.getObject() );
				if ( platformAgentRepository.exists( roleMemberIRI ) ) {
					transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> {
						addRoleToAppRoleMap( roleMemberIRI, app, roleIRI );
					} );
				}
			}
		}
	}

	private void addRoleToAppRoleMap( IRI roleMemberIRI, App app, IRI roleIRI ) {
		Agent agent = platformAgentRepository.get( roleMemberIRI );
		IRI appRoleMapIRI = agent.getIRI( PlatformAgentDescription.Property.APP_ROLE_MAP );
		mapRepository.add( appRoleMapIRI, app.getIRI(), roleIRI );
	}
}
