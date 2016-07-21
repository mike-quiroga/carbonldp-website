package com.carbonldp.agents.app;

import com.carbonldp.agents.SesameAgentsRepository;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFResource;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author NestorVenegas
 * @since 0.14.0-ALPHA
 */

@Transactional
public class SesameAppAgentRepository extends SesameAgentsRepository implements AppAgentRepository {

	public SesameAppAgentRepository( SesameConnectionFactory connectionFactory, RDFSourceRepository sourceRepository, ContainerRepository containerRepository ) {
		super( connectionFactory, sourceRepository, containerRepository );
	}

	public Container createAppAgentsContainer( IRI rootContainerIRI ) {
		IRI appAgentsContainerIRI = getContainerIRI( rootContainerIRI );
		BasicContainer appAgentsContainer = BasicContainerFactory.getInstance().create( new RDFResource( appAgentsContainerIRI ) );
		containerRepository.createChild( rootContainerIRI, appAgentsContainer );
		return appAgentsContainer;
	}

	@Override
	public IRI getAgentsContainerIRI() {
		IRI appIRI = AppContextHolder.getContext().getApplication().getRootContainerIRI();
		if ( appIRI == null ) throw new RuntimeException( "app agent repository should be running in App context" );
		return getContainerIRI( appIRI );
	}

	public void setAgentsContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.containerSlug = slug;
	}
}
