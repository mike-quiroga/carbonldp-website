package com.carbonldp.agents;

import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.RDFNodeUtil;
import org.openrdf.model.IRI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.spring.SesameConnectionFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Nestor
 * @since 0.14.0-ALPHA
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

	private static final String userSelector;

	static {
		userSelector = RDFNodeUtil.generatePredicateStatement( "?members", "?user", LDAPAgentDescription.Property.USER_CREDENTIALS );
	}

	@Override
	public boolean exists( IRI agentIRI ) {
		return containerRepository.hasMember( getAgentsContainerIRI(), agentIRI, agentsContainerType );
	}

	@Override
	public boolean existsWithEmail( String email ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "email", SimpleValueFactory.getInstance().createLiteral( email ) );
		return containerRepository.hasMembers( getAgentsContainerIRI(), emailSelector, bindings );
	}

	@Override
	public Agent get( IRI uri ) {
		return new Agent( sourceRepository.get( uri ) );
	}

	@Override
	public Agent findByEmail( String email ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "email", SimpleValueFactory.getInstance().createLiteral( email ) );

		Set<IRI> memberIRIs = containerRepository.findMembers( getAgentsContainerIRI(), emailSelector, bindings, agentsContainerType );
		if ( memberIRIs.isEmpty() ) return null;
		if ( memberIRIs.size() > 1 ) {
			// TODO: Add error number
			throw new IllegalStateException( "Two agents with the same email were found." );
		}

		IRI agentIRI = memberIRIs.iterator().next();

		RDFSource agentSource = sourceRepository.get( agentIRI );
		if ( agentSource == null ) return null;

		return new Agent( agentSource.getBaseModel(), agentIRI );
	}

	@Override
	public Set<Agent> findByUID( String user ) {
		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "user", SimpleValueFactory.getInstance().createLiteral( user ) );

		Set<IRI> memberIRIs = containerRepository.findMembers( getAgentsContainerIRI(), userSelector, bindings, agentsContainerType );
		if ( memberIRIs.isEmpty() ) return null;

		Set<Agent> agents = new HashSet<>();
		for ( IRI agentIRI : memberIRIs ) {
			RDFSource agentSource = sourceRepository.get( agentIRI );
			if ( agentSource == null ) continue;
			agents.add( new LDAPAgent( agentSource.getBaseModel(), agentIRI ) );
		}

		return agents;
	}

	@Override
	public void create( Agent agent ) {
		containerRepository.createChild( getAgentsContainerIRI(), agent, agentsContainerType );
	}

	protected abstract IRI getAgentsContainerIRI();
}
