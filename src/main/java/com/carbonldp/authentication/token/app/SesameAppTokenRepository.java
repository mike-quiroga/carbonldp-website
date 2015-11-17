package com.carbonldp.authentication.token.app;

import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.URIUtil;
import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.util.Assert;

/**
 * @author NestorVenegas
 * @since 0.15.0_ALPHA
 */
public class SesameAppTokenRepository extends AbstractSesameRepository implements AppTokenRepository {

	private String containerSlug;
	private ContainerRepository containerRepository;

	public SesameAppTokenRepository( SesameConnectionFactory connectionFactory, ContainerRepository containerRepository ) {
		super( connectionFactory );
		this.containerRepository = containerRepository;
	}

	@Override
	public Container createAppTokensContainer( URI rootContainerURI ) {
		URI appTokensContainerURI = getContainerURI( rootContainerURI );
		BasicContainer appTokensContainer = BasicContainerFactory.getInstance().create( new RDFResource( appTokensContainerURI ) );
		containerRepository.createChild( rootContainerURI, appTokensContainer );
		return appTokensContainer;
	}

	private URI getContainerURI( URI rootContainerURI ) {
		return URIUtil.createChildURI( rootContainerURI, containerSlug );
	}

	public void setTokensContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.containerSlug = slug;
	}
}
