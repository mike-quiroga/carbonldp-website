package com.carbonldp.authentication.ldapServer.app;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.agents.LDAPAgent;
import com.carbonldp.agents.LDAPAgentFactory;
import com.carbonldp.apps.App;
import com.carbonldp.authentication.LDAPServer;
import com.carbonldp.authentication.LDAPServerFactory;
import com.carbonldp.authentication.token.JWTUtil;
import com.carbonldp.exceptions.InvalidResourceException;
import com.carbonldp.exceptions.LDAPException;
import com.carbonldp.ldp.AbstractSesameLDPService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.models.Infraction;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.LDAPUtil;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ldap.*;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.filter.PresentFilter;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since 0.37.0
 */
public class SesameLDAPServerService extends AbstractSesameLDPService implements LDAPServerService {

	protected ContainerService containerService;
	protected AgentRepository agentRepository;
	protected RDFSourceService sourceService;

	@Override
	public void create( IRI targetIRI, LDAPServer ldapServer ) {
		validate( ldapServer );
		String passwordEncoded = JWTUtil.encode( ldapServer.getPassword() );
		ldapServer.setPassword( passwordEncoded );
		containerService.createChild( targetIRI, ldapServer );
	}

	@Override
	public LDAPServer get( IRI targetIRI ) {
		return new LDAPServer( sourceService.get( targetIRI ) );
	}

	@Override
	public List<LDAPAgent> registerLDAPAgents( LDAPServer ldapServer, Set<String> usernameFields, App app ) {
		IRI agentsContainerIRI = agentRepository.getAgentsContainerIRI();
		LdapTemplate ldapTemplate;
		String encodedPassword = ldapServer.getPassword();
		ldapServer.setPassword( JWTUtil.decode( encodedPassword ) );
		try {
			ldapTemplate = LDAPUtil.getLDAPTemplate( ldapServer );
		} catch ( UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e ) {
			throw new LDAPException( new Infraction( 0x2601 ) );
		}
		OrFilter orFilter = new OrFilter();
		for ( String field : usernameFields ) { orFilter.or( new PresentFilter( field ) ); }
		ContactAttributeMapperLDAPAgent attributeMapper = new ContactAttributeMapperLDAPAgent();
		attributeMapper.setAgentsContainerIRI( agentsContainerIRI );
		attributeMapper.setUsernameFields( usernameFields );
		List<LDAPAgent> agents = new ArrayList<>();
		try {
			agents = ldapTemplate.search( "", orFilter.encode(), attributeMapper );
		} catch ( AuthenticationException e ) {
			throw new LDAPException( new Infraction( 0x2601 ) );
		} catch ( CommunicationException e ) {
			throw new LDAPException( new Infraction( 0x2602 ) );
		} catch ( UncategorizedLdapException e ) {
			throw new LDAPException( new Infraction( 0x2603 ) );
		} catch ( ServiceUnavailableException e ) {
			throw new LDAPException( new Infraction( 0x2604 ) );
		} catch ( NameNotFoundException e ) {
			throw new LDAPException( new Infraction( 0x2605 ) );
		}
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

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) {
		this.sourceService = sourceService;
	}
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
