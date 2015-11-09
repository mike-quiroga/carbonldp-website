package com.carbonldp.agents;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Nestor
 * @since 0.14.0_ALPHA
 */
public abstract class SesameAgentsRepository extends AbstractSesameRepository implements AgentRepository {
	protected final RDFSourceRepository sourceRepository;
	protected final ContainerRepository containerRepository;

	protected final Type agentsContainerType = Type.BASIC;

	public SesameAgentsRepository( SesameConnectionFactory connectionFactory, RDFSourceRepository sourceRepository, ContainerRepository containerRepository ) {
		super( connectionFactory );
		this.sourceRepository = sourceRepository;
		this.containerRepository = containerRepository;
	}

	private static final String emailSelector;

	static {
		emailSelector = RDFNodeUtil.generatePredicateStatement( "?members", "?email", AgentDescription.Property.EMAIL );
	}

	@Override
	public boolean exists( URI agentURI ) {
		return containerRepository.hasMember( getAgentsContainerURI(), agentURI, agentsContainerType );
	}

	@Override
	public boolean existsWithEmail( String email ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "email", ValueFactoryImpl.getInstance().createLiteral( email ) );
		return containerRepository.hasMembers( getAgentsContainerURI(), emailSelector, bindings );
	}

	@Override
	public Agent get( URI uri ) {
		return null;
	}

	@Override
	public Agent findByEmail( String email ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "email", ValueFactoryImpl.getInstance().createLiteral( email ) );

		Set<URI> memberURIs = containerRepository.findMembers( getAgentsContainerURI(), emailSelector, bindings, agentsContainerType );
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

	@Override
	public void create( Agent agent ) {
		containerRepository.createChild( getAgentsContainerURI(), agent, agentsContainerType );
	}

	protected abstract URI getAgentsContainerURI();
}
