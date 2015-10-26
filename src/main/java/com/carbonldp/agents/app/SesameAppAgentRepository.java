package com.carbonldp.agents.app;

import com.carbonldp.agents.Agent;
import com.carbonldp.agents.AgentDescription;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.URIUtil;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author NestorVenegas
 * @since _version_
 */

@Transactional
public class SesameAppAgentRepository extends AbstractSesameRepository implements AppAgentRepository {

	private final RDFSourceRepository sourceRepository;
	private final ContainerRepository containerRepository;

	private String containerSlug;

	private static final String existsWithEmailSelector;

	static {
		existsWithEmailSelector = RDFNodeUtil.generatePredicateStatement( "?members", "?email", AgentDescription.Property.EMAIL );
	}

	public SesameAppAgentRepository( SesameConnectionFactory connectionFactory, RDFSourceRepository sourceRepository, ContainerRepository containerRepository ) {
		super( connectionFactory );
		this.sourceRepository = sourceRepository;
		this.containerRepository = containerRepository;
	}

	@Override
	public Container createAppRolesContainer( URI rootContainerURI ) {
		URI appRolesContainerURI = getContainerURI( rootContainerURI );
		BasicContainer appAgentsContainer = BasicContainerFactory.getInstance().create( new RDFResource( appRolesContainerURI ) );
		containerRepository.createChild( rootContainerURI, appAgentsContainer );
		return appAgentsContainer;
	}

	public boolean existsWithEmail( String email ) {
		URI appURI = AppContextHolder.getContext().getApplication().getURI();
		// TODO: throw error if there's no app context
		URI agentsContainerURI = new URIImpl( appURI.stringValue() + containerSlug );

		Map<String, Value> bindings = new HashMap<>();
		bindings.put( "email", ValueFactoryImpl.getInstance().createLiteral( email ) );

		return containerRepository.hasMembers( agentsContainerURI, existsWithEmailSelector, bindings );
	}

	@Override
	public void create( URI appURI, Agent agent ) {
		URI containerURI = getContainerURI( appURI );
		containerRepository.createChild( containerURI, agent, containerRepository.getContainerType( containerURI ) );
	}

	private URI getContainerURI( URI rootContainerURI ) {
		return URIUtil.createChildURI( rootContainerURI, containerSlug );
	}

	public void setAgentsContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.containerSlug = slug;
	}
}
