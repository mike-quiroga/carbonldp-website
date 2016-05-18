package com.carbonldp.authentication.ldapServer.app;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.agents.LDAPAgent;
import com.carbonldp.agents.LDAPAgentFactory;
import com.carbonldp.apps.App;
import com.carbonldp.authentication.LDAPServer;
import com.carbonldp.authentication.LDAPServerFactory;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.LDAPUtil;
import org.openrdf.model.IRI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.filter.PresentFilter;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;
import java.util.Set;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since _version_
 */
public class SesameLDAPServerService extends AbstractSesameLDPService implements LDAPServerService {

	private ContainerService containerService;
	AgentRepository agentRepository;

	@Override
	public void create( IRI targetIRI, LDAPServer ldapServer ) {
		validate( ldapServer );
		containerService.createChild( targetIRI, ldapServer );
	}

	@Override
	public LDAPServer get( IRI targetIRI ) {
		return new LDAPServer( sourceRepository.get( targetIRI ) );
	}

	@Override
	public List<LDAPAgent> registerLDAPAgents( LDAPServer ldapServer, Set<String> usernameFields, App app ) {
		IRI agentsContainerIRI = agentRepository.getAgentsContainerIRI();
		LdapTemplate ldapTemplate = LDAPUtil.getLDAPTemplate( ldapServer );
		OrFilter orFilter = new OrFilter();
		for ( String field : usernameFields ) { orFilter.or( new PresentFilter( field ) ); }
		ContactAttributeMapperLDAPAgent attributeMapper = new ContactAttributeMapperLDAPAgent();
		attributeMapper.setAgentsContainerIRI( agentsContainerIRI );
		attributeMapper.setUsernameFields( usernameFields );

		List<LDAPAgent> agents = ldapTemplate.search( "", orFilter.encode(), attributeMapper );
		IRI ldapServerIRI = ldapServer.getIRI();
		for ( LDAPAgent agent : agents ) {
			agent.setLDAPServer( ldapServerIRI );
			containerService.createChild( agentsContainerIRI, agent );
		}
		return agents;
	}

	private void validate( LDAPServer ldapServer ) {
		List<Infraction> infractions = LDAPServerFactory.getInstance().validate( ldapServer );
		if ( ! infractions.isEmpty() ) throw new InvalidResourceException( infractions );
	}

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }

	@Autowired
	@Qualifier( "appAgentRepository" )
	public void setAgentRepository( AgentRepository agentRepository ) { this.agentRepository = agentRepository;}
}

class ContactAttributeMapperLDAPAgent implements AttributesMapper<LDAPAgent> {

	ValueFactory valueFactory = SimpleValueFactory.getInstance();
	IRI agentsContainerIRI;
	Set<String> usernameFields;

	@Override
	public LDAPAgent mapFromAttributes( Attributes attributes ) throws NamingException {
		IRI agentIRI = valueFactory.createIRI( agentsContainerIRI.stringValue() + IRIUtil.createRandomSlug() + "/" );
		NamingEnumeration<String> ids = attributes.getIDs();
		RDFResource resource = new RDFResource( agentIRI );
		LDAPAgent ldapAgent = LDAPAgentFactory.getInstance().create( resource );

		while ( ids.hasMoreElements() ) {
			String id = ids.next();
			if ( ! usernameFields.contains( id ) ) continue;
			ldapAgent.addUserCredentials( id, attributes.get( id ).get().toString() );
		}
		return ldapAgent;
	}

	public void setAgentsContainerIRI( IRI agentsContainerIRI ) {
		this.agentsContainerIRI = agentsContainerIRI;
	}

	public void setUsernameFields( Set<String> usernameFields ) {
		this.usernameFields = usernameFields;
	}
}
