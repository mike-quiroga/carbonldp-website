package com.carbonldp.authorization;

import com.carbonldp.agents.Agent;
import com.carbonldp.descriptions.ContainerDescription.Type;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.models.RDFSource;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.URIUtil;
import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;

import java.util.HashSet;
import java.util.Set;

public class SesamePlatformRoleRepository extends AbstractSesameRepository implements PlatformRoleRepository {
	private final RDFSourceRepository sourceService;
	private final ContainerRepository containerRepository;
	private final URI platformRolesContainerURI;

	private final Type platformRolesContainerType = Type.BASIC;

	public SesamePlatformRoleRepository( SesameConnectionFactory connectionFactory, RDFSourceRepository sourceService, ContainerRepository containerRepository,
		URI platformRolesContainerURI ) {
		super( connectionFactory );
		this.sourceService = sourceService;
		this.containerRepository = containerRepository;
		this.platformRolesContainerURI = platformRolesContainerURI;
	}

	public Set<PlatformRole> get( Agent agent ) {
		Set<PlatformRole> platformRoles = new HashSet<PlatformRole>();
		Set<URI> platformRolesURIs = containerRepository.filterMembers( platformRolesContainerURI, agent.getPlatformRoles(), platformRolesContainerType );
		Set<RDFSource> sources = sourceService.get( platformRolesURIs );
		for ( RDFSource source : sources ) {
			platformRoles.add( new PlatformRole( source ) );
		}
		return platformRoles;
	}

	public Set<Platform.Role> getRepresentations( Set<PlatformRole> platformRoleResources ) {
		Set<URI> platformRolesURIs = URIUtil.getURIs( platformRoleResources );
		return RDFNodeUtil.findByURIs( platformRolesURIs, Platform.Role.class );
	}
}
