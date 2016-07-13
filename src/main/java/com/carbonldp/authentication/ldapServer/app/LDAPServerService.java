package com.carbonldp.authentication.ldapServer.app;

import com.carbonldp.agents.LDAPAgent;
import com.carbonldp.apps.App;
import com.carbonldp.authentication.LDAPServer;
import org.eclipse.rdf4j.model.IRI;

import java.util.List;
import java.util.Set;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since 0.37.0
 */
public interface LDAPServerService {

	public void create( IRI targetIRI, LDAPServer ldapServer );

	public LDAPServer get( IRI targetIRI );

	public List<LDAPAgent> registerLDAPAgents( LDAPServer ldapServer, Set<String> usernameFields,App app );
}
