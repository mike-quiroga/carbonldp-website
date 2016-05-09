package com.carbonldp.authentication.LDAP.app;

import com.carbonldp.ldp.containers.BasicContainer;
import com.carbonldp.ldp.containers.BasicContainerFactory;
import com.carbonldp.ldp.containers.Container;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.rdf.RDFResource;
import com.carbonldp.repository.AbstractSesameRepository;
import org.openrdf.model.IRI;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @author JorgeEspinosa
 * @author NestorVenegas
 * @since _version_
 */
public class SesameAppLDAPServerRepository extends AbstractSesameRepository implements AppLDAPServerRepository {

	private ContainerRepository containerRepository;

	public SesameAppLDAPServerRepository( SesameConnectionFactory connectionFactory ) {
		super( connectionFactory );
	}

	public Container createAppLDAPServersContainer( IRI rootContainerIRI ) {
		IRI appLDAPServerContainerIRI = getContainerIRI( rootContainerIRI );
		BasicContainer appLDAPServersContainer = BasicContainerFactory.getInstance().create( new RDFResource( appLDAPServerContainerIRI ) );
		containerRepository.createChild( rootContainerIRI, appLDAPServersContainer );
		return appLDAPServersContainer;
	}

	public void setLDAPServerContainerSlug( String slug ) {
		Assert.notNull( slug );
		this.containerSlug = slug;
	}

	@Autowired
	public void setContainerRepository( ContainerRepository containerRepository ) { this.containerRepository = containerRepository; }
}
