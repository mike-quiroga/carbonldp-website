package com.carbonldp.repository.updates;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.PlatformAgentDescription;
import com.carbonldp.agents.platform.SesamePlatformAgentService;
import com.carbonldp.apps.App;
import com.carbonldp.rdf.RDFMap;
import com.carbonldp.utils.ValueUtil;
import org.openrdf.model.BNode;
import org.openrdf.model.IRI;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.springframework.aop.framework.Advised;

import java.util.Set;

/**
 * @author JorgeEspinosa
 * @since _version_
 */
public class UpdateAction1o10o0 extends AbstractUpdateAction {

	@Override
	protected void execute() throws Exception {
		createRDFMaps();
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> {
				addRolesToAgentMaps( app );
			} );
		}
	}

	private void createRDFMaps() throws Exception {
		Set<IRI> platformAgentIRIs = containerRepository.getMemberIRIs( platformAgentRepository.getAgentsContainerIRI() );
		SesamePlatformAgentService sesamePlatformAgentService = (SesamePlatformAgentService) ( (Advised) platformAgentService ).getTargetSource().getTarget();
		transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> {
			for ( IRI platformAgentIRI : platformAgentIRIs ) {
				Agent agentResource = platformAgentRepository.get( platformAgentIRI );
				if ( agentResource.getIRI( PlatformAgentDescription.Property.APP_ROLE_MAP ) == null && ! platformAgentIRI.stringValue().equals( platformAgentRepository.getAgentsContainerIRI().stringValue() + "admin/" ) ) {
					sesamePlatformAgentService.createAppRoleMap( agentResource );
				}
			}
		} );
	}

	private void addRolesToAgentMaps( App app ) {
		IRI appRoleContainerIRI = appRoleRepository.getContainerIRI();
		Set<Statement> members = containerService.getMembershipTriples( appRoleContainerIRI );
		for ( Statement member : members ) {
			IRI roleIRI = ValueUtil.getIRI( member.getObject() );
			Set<Statement> roleMembers = containerService.getMembershipTriples( valueFactory.createIRI( roleIRI.stringValue() + "agents/" ) );
			for ( Statement roleMember : roleMembers ) {
				IRI roleMemberIRI = ValueUtil.getIRI( roleMember.getObject() );
				if ( platformAgentRepository.exists( roleMemberIRI ) ) {
					transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> {
						addRoleToAgentMap( roleMemberIRI, app, roleIRI );
					} );
				}
			}
		}
	}

	private void addRoleToAgentMap( IRI roleMemberIRI, App app, IRI roleIRI ) {
		Agent agent = platformAgentRepository.get( roleMemberIRI );
		IRI rdfMapIRI = agent.getIRI( PlatformAgentDescription.Property.APP_ROLE_MAP );
		if ( rdfMapIRI == null ) return;
		mapRepository.clean( rdfMapIRI );
		mapRepository.add( rdfMapIRI, app.getIRI(), roleIRI );
	}
}
