package com.carbonldp.authorization;

import com.carbonldp.agents.Agent;
import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.RDFNodeUtil;
import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;

import java.util.Set;
import java.util.stream.Collectors;

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

	public Set<PlatformRole> get( Set<URI> platformRoleURIs ) {
		Set<URI> platformRolesURIs = containerRepository.filterMembers( platformRolesContainerURI, platformRoleURIs, platformRolesContainerType );
		Set<RDFSource> sources = sourceService.get( platformRolesURIs );

		return sources.stream().map( PlatformRole::new ).collect( Collectors.toSet() );
	}

	public Set<PlatformRole> get( Agent agent ) {
		return this.get( agent.getPlatformRoles() );
	}

	public Set<Platform.Role> getRepresentations( Set<PlatformRole> platformRoleResources ) {
		Set<URI> platformRolesURIs = IRIUtil.getIRIs( platformRoleResources );
		return RDFNodeUtil.findByIRIs( platformRolesURIs, Platform.Role.class );
	}
}
