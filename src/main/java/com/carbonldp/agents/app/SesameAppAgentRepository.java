package com.carbonldp.agents.app;

import com.carbonldp.ldp.AbstractSesameLDPRepository;
import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.utils.URIUtil;
import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.util.Assert;

/**
 * @author NestorVenegas
 * @since _version_
 */
public class SesameAppAgentRepository extends AbstractSesameLDPRepository implements AppAgentRepository {

	private final RDFSourceRepository sourceRepository;
	private final ContainerRepository containerRepository;

	private String containerSlug;

	public SesameAppAgentRepository( SesameConnectionFactory connectionFactory, RDFResourceRepository resourceRepository, RDFDocumentRepository documentRepository, RDFSourceRepository sourceRepository, ContainerRepository containerRepository ) {
		super( connectionFactory, resourceRepository, documentRepository );

		Assert.notNull( sourceRepository );
		Assert.notNull( documentRepository );
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

	private URI getContainerURI( URI rootContainerURI ) {
		return URIUtil.createChildURI( rootContainerURI, containerSlug );
	}

	public void setAgentsContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.containerSlug = slug;
	}
}
