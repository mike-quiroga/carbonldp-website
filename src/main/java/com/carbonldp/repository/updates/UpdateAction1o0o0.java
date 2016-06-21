package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentFactory;
import com.carbonldp.authorization.Platform;
import com.carbonldp.rdf.RDFResource;
import org.openrdf.model.IRI;
import org.openrdf.model.Resource;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.repository.RepositoryException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MiguelAraCo
 * @since 0.9.0-ALPHA
 */
public class UpdateAction1o0o0 extends AbstractUpdateAction {

	private static String configurationFile;
	private static final String resourcesFile = "platform-default.trig";
	private static final String usernameLine = "platform.system.agent.username";
	private static final String passwordLine = "platform.system.agent.password";

	@Override
	public void execute() throws Exception {
		emptyRepository();
		loadResourcesFile( resourcesFile, Vars.getInstance().getHost() );
		createSystemUser( Vars.getInstance().getSystemUser(), Vars.getInstance().getSystemPass() );
	}

	public void setConfigurationFile( String configurationFile ) {
		this.configurationFile = configurationFile;
	}

	protected void emptyRepository() {
		transactionWrapper.runInPlatformContext( () -> {
			try {
				connectionFactory.getConnection().remove( (Resource) null, null, null );
			} catch ( RepositoryException e ) {
				throw new RuntimeException( e );
			}
		} );
	}

	private void createSystemUser( String userName, String password ) {
		transactionWrapper.runWithAnonymousRoleInPlatformContext( () -> {
			IRI agentIRI = valueFactory.createIRI( platformAgentRepository.getAgentsContainerIRI().stringValue() + "system/" );
			RDFResource resource = new RDFResource( agentIRI );
			Agent agent = AgentFactory.getInstance().create( resource );
			agent.setEmail( userName );
			agent.setPassword( password );
			platformAgentService.register( agent );
			containerRepository.addMember( SimpleValueFactory.getInstance().createIRI( Platform.Role.SYSTEM.getIRI().stringValue() + "agents/" ), agent.getIRI() );
			List<String> toRemove = new ArrayList<>();
			toRemove.add( usernameLine );
			toRemove.add( passwordLine );
			localFileRepository.removeLineFromFileStartingWith( configurationFile, toRemove );
		} );
	}
}
