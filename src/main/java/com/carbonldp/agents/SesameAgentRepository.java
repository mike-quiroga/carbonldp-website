package com.carbonldp.agents;

import com.carbonldp.descriptions.ContainerDescription.Type;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.RDFSource;
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
public class SesameAgentRepository extends AbstractSesameRepository implements AgentRepository {
	private final RDFSourceRepository sourceService;
	private final ContainerRepository containerRepository;
	private final URI agentsContainerURI;

	private final Type agentsContainerType = Type.BASIC;

	public SesameAgentRepository( SesameConnectionFactory connectionFactory, RDFSourceRepository sourceService, ContainerRepository containerRepository,
		URI agentsContainerURI ) {
		super( connectionFactory );
		this.sourceService = sourceService;
		this.containerRepository = containerRepository;
		this.agentsContainerURI = agentsContainerURI;
	}

	private static final String findByEmail_selector;

	static {
		StringBuilder queryBuilder = new StringBuilder();
		//@formatter:off
		queryBuilder
				.append( RDFNodeUtil.generatePredicateStatement( "?members", "?email", AgentDescription.Property.EMAIL ) )
		;
		//@formatter:on
		findByEmail_selector = queryBuilder.toString();
	}

	public Agent findByEmail( String email ) {
		Map<String, Value> bindings = new HashMap<String, Value>();
		bindings.put( "email", ValueFactoryImpl.getInstance().createLiteral( email ) );

		Set<URI> memberURIs = containerRepository.findMembers( agentsContainerURI, findByEmail_selector, bindings, agentsContainerType );
		if ( memberURIs.isEmpty() ) return null;
		if ( memberURIs.size() > 1 ) {
			// TODO: Add error number
			throw new IllegalStateException( "Two agents with the same email were found." );
		}

		URI agentURI = memberURIs.iterator().next();

		RDFSource agentSource = sourceService.get( agentURI );
		if ( agentSource == null ) return null;

		return new Agent( agentSource.getBaseModel(), agentURI );
	}

	public Agent findByURI( URI uri ) {
		// TODO
		return null;
	}

}
