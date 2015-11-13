package com.carbonldp.agents.app;

import com.carbonldp.agents.SesameAgentsRepository;
import com.carbonldp.apps.context.AppContextHolder;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.utils.URIUtil;
import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * @author NestorVenegas
 * @since 0.14.0-ALPHA
 */

@Transactional
public class SesameAppAgentRepository extends SesameAgentsRepository implements AppAgentRepository {

	private String containerSlug;

	public SesameAppAgentRepository( SesameConnectionFactory connectionFactory, RDFSourceRepository sourceRepository, ContainerRepository containerRepository ) {
		super( connectionFactory, sourceRepository, containerRepository );
	}

	public Container createAppAgentsContainer( URI rootContainerURI ) {
		URI appRolesContainerURI = getContainerURI( rootContainerURI );
		BasicContainer appAgentsContainer = BasicContainerFactory.getInstance().create( new RDFResource( appRolesContainerURI ) );
		containerRepository.createChild( rootContainerURI, appAgentsContainer );
		return appAgentsContainer;
	}

	@Override
	protected URI getAgentsContainerURI() {
		URI appURI = AppContextHolder.getContext().getApplication().getRootContainerURI();
		if ( appURI == null ) throw new RuntimeException( "app agent repository should be running in App context" );
		return getContainerURI( appURI );
	}

	private URI getContainerURI( URI rootContainerURI ) {
		return URIUtil.createChildURI( rootContainerURI, containerSlug );
	}

	public void setAgentsContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.containerSlug = slug;
	}
}
