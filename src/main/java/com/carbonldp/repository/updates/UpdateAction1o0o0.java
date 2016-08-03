package com.carbonldp.repository.updates;

import com.carbonldp.Vars;
import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentFactory;
import com.carbonldp.authorization.Platform;
import com.carbonldp.rdf.RDFResource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.repository.RepositoryException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author MiguelAraCo
 * @since 0.9.0-ALPHA
 */
public class UpdateAction1o0o0 extends AbstractUpdateAction {

	private static String configurationFile;
	private static final String resourcesFile = "platform-default.trig";
	private static final String usernameLine = "platform.system-agent.username";
	private static final String passwordLine = "platform.system-agent.password";

	@Override
	public void execute() throws Exception {
		emptyRepository();
		loadResourcesFile( resourcesFile, Vars.getInstance().getHost() );
		if ( Vars.getInstance().getSystemUser() == null || Vars.getInstance().getSystemPass() == null ) {
			throw new RuntimeException( "Configure the system user into the config.properties file" );
		}
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
		Agent agent = createSystemAgent( userName, password );
		transactionWrapper.runWithSystemPermissionsInPlatformContext( () -> {
			IRI agentsContainerIRI = platformAgentRepository.getAgentsContainerIRI();
			platformAgentService.create( agentsContainerIRI, agent );
			containerRepository.addMember( SimpleValueFactory.getInstance().createIRI( Platform.Role.SYSTEM.getIRI().stringValue() + "agents/" ), agent.getIRI() );
		} );
		removeSystemCredentials();
	}

	private void removeSystemCredentials() {
		List<String> toRemove = new ArrayList<>();
		toRemove.add( usernameLine );
		toRemove.add( passwordLine );
		localFileRepository.removeLineFromFileStartingWith( configurationFile, toRemove );
	}

	private Agent createSystemAgent( String userName, String password ) {
		IRI agentIRI = valueFactory.createIRI( platformAgentRepository.getAgentsContainerIRI().stringValue() + "system/" );
		RDFResource resource = new RDFResource( agentIRI );
		Agent agent = AgentFactory.getInstance().create( resource );
		agent.setEnabled( true );
		agent.setEmail( userName );
		agent.setPassword( password );
		return agent;
	}
}