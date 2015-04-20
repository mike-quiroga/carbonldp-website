package com.carbonldp.agents.platform;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentDescription;
import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.RDFNodeUtil;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Transactional
public class SesamePlatformAgentRepository extends AbstractSesameRepository implements PlatformAgentRepository {
	private final RDFSourceRepository sourceRepository;
	private final ContainerRepository containerRepository;
	private final URI agentsContainerURI;

	private final Type agentsContainerType = Type.BASIC;

	public SesamePlatformAgentRepository( SesameConnectionFactory connectionFactory, RDFSourceRepository sourceRepository, ContainerRepository containerRepository,
		URI agentsContainerURI ) {
		super( connectionFactory );
		this.sourceRepository = sourceRepository;
		this.containerRepository = containerRepository;
		this.agentsContainerURI = agentsContainerURI;
	}

	public boolean exists( URI agentURI ) {
		return containerRepository.hasMember( agentsContainerURI, agentURI, agentsContainerType );
	}

	private static final String existsWithEmailSelector;

	static {
		existsWithEmailSelector = RDFNodeUtil.generatePredicateStatement( "?members", "?email", AgentDescription.Property.EMAIL );
	}

	public boolean existsWithEmail( String email ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "email", ValueFactoryImpl.getInstance().createLiteral( email ) );
		return containerRepository.hasMembers( agentsContainerURI, existsWithEmailSelector, bindings );
	}

	public Agent get( URI uri ) {
		// TODO
		return null;
	}

	private static final String findByEmailSelector;

	static {
		findByEmailSelector = RDFNodeUtil.generatePredicateStatement( "?members", "?email", AgentDescription.Property.EMAIL );
	}

	public Agent findByEmail( String email ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "email", ValueFactoryImpl.getInstance().createLiteral( email ) );

		Set<URI> memberURIs = containerRepository.findMembers( agentsContainerURI, findByEmailSelector, bindings, agentsContainerType );
		if ( memberURIs.isEmpty() ) return null;
		if ( memberURIs.size() > 1 ) {
			// TODO: Add error number
			throw new IllegalStateException( "Two agents with the same email were found." );
		}

		URI agentURI = memberURIs.iterator().next();

		RDFSource agentSource = sourceRepository.get( agentURI );
		if ( agentSource == null ) return null;

		return new Agent( agentSource.getBaseModel(), agentURI );
	}

	public void create( Agent agent ) {

	}

}
