package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import com.carbonldp.apps.App;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.IRIUtil;
import org.eclipse.rdf4j.model.IRI;

import java.util.Set;

/**
 * add agent me container
 * @author NestorVenegas
 * @since _version_
 */
public class UpdateAction1o13o0 extends AbstractUpdateAction {
	@Override
	protected void execute() throws Exception {
		transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> {
			IRI agentMe = valueFactory.createIRI( Vars.getInstance().getPlatformAgentMeURL() );
			BasicContainer agentMeContainer = BasicContainerFactory.getInstance().create( new RDFResource( agentMe ) );
			IRI agents = valueFactory.createIRI( Vars.getInstance().getAgentsContainerURL() );
			containerRepository.createChild( agents, agentMeContainer );
			aclRepository.createACL( agentMe );
		} );
		Set<App> apps = getAllApps();
		for ( App app : apps ) {
			transactionWrapper.runWithSystemPermissionsInAppContext( app, () -> {
				IRI agentsContainerIRI = IRIUtil.createChildIRI( app.getRootContainerIRI(), Vars.getInstance().getAppAgentsContainer() );
				IRI agentMeContainerIRI = IRIUtil.createChildIRI( agentsContainerIRI, Vars.getInstance().getAppAgentMeContainer() );
				BasicContainer agentMeContainer = BasicContainerFactory.getInstance().create( new RDFResource( agentMeContainerIRI ) );
				containerRepository.createChild( agentsContainerIRI, agentMeContainer );
				aclRepository.createACL( agentMeContainer.getIRI() );
			} );
		}
	}
}
