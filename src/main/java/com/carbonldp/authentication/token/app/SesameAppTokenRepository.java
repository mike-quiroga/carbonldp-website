package com.carbonldp.authentication.token.app;

import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.IRIUtil;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.springframework.util.Assert;

/**
 * @author NestorVenegas
 * @since 0.15.0_ALPHA
 */
public class SesameAppTokenRepository extends AbstractSesameRepository implements AppTokenRepository {

	private String tokensContainerSlug;
	private String ticketsContainerSlug;
	private ContainerRepository containerRepository;

	public SesameAppTokenRepository( SesameConnectionFactory connectionFactory, ContainerRepository containerRepository ) {
		super( connectionFactory );
		this.containerRepository = containerRepository;
	}

	@Override
	public Container createAppTokensContainer( IRI rootContainerIRI ) {
		IRI appTokensContainerIRI = getTokensContainerIRI( rootContainerIRI );
		BasicContainer appTokensContainer = BasicContainerFactory.getInstance().create( new RDFResource( appTokensContainerIRI ) );
		containerRepository.createChild( rootContainerIRI, appTokensContainer );
		return appTokensContainer;
	}

	@Override
	public Container createTicketsContainer( IRI rootContainerIRI ) {
		IRI appTicketsContainerIRI = getTicketsContainerIRI( rootContainerIRI );
		BasicContainer appTicketsContainer = BasicContainerFactory.getInstance().create( new RDFResource( appTicketsContainerIRI ) );
		containerRepository.createChild( rootContainerIRI, appTicketsContainer );
		return appTicketsContainer;
	}

	private IRI getTokensContainerIRI( IRI rootContainerIRI ) {
		return IRIUtil.createChildIRI( rootContainerIRI, tokensContainerSlug );
	}

	private IRI getTicketsContainerIRI( IRI rootContainerIRI ) {
		return IRIUtil.createChildIRI( rootContainerIRI, ticketsContainerSlug );
	}

	public void setTokensContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.tokensContainerSlug = slug;
	}

	public void setTicketsContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.ticketsContainerSlug = slug;
	}
}
