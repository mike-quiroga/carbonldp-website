package com.carbonldp.authentication.token.app;

import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.IRIUtil;
import org.openrdf.model.IRI;
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
	public Container createAppTokensContainer( IRI rootContainerIRI ) {
		IRI appTokensContainerIRI = getContainerIRI( rootContainerIRI );
		BasicContainer appTokensContainer = BasicContainerFactory.getInstance().create( new RDFResource( appTokensContainerIRI ) );
		containerRepository.createChild( rootContainerIRI, appTokensContainer );
		return appTokensContainer;
	}

	private IRI getContainerIRI( IRI rootContainerIRI ) {
		return IRIUtil.createChildIRI( rootContainerIRI, containerSlug );
	}

	public void setTokensContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.containerSlug = slug;
	}
}
