package com.carbonldp.authorization;

import com.carbonldp.agents.Agent;
import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.RDFNodeUtil;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;

import java.util.Set;
import java.util.stream.Collectors;

public class SesamePlatformRoleRepository extends AbstractSesameRepository implements PlatformRoleRepository {
	private final RDFSourceRepository sourceService;
	private final ContainerRepository containerRepository;
	private final IRI platformRolesContainerIRI;

	private final Type platformRolesContainerType = Type.BASIC;

	public SesamePlatformRoleRepository( SesameConnectionFactory connectionFactory, RDFSourceRepository sourceService, ContainerRepository containerRepository,
		IRI platformRolesContainerIRI ) {
		super( connectionFactory );
		this.sourceService = sourceService;
		this.containerRepository = containerRepository;
		this.platformRolesContainerIRI = platformRolesContainerIRI;
	}

	public Set<PlatformRole> get( Set<IRI> platformRoleIRIs ) {
		Set<IRI> platformRolesIRIs = containerRepository.filterMembers( platformRolesContainerIRI, platformRoleIRIs, platformRolesContainerType );
		Set<RDFSource> sources = sourceService.get( platformRolesIRIs );

		return sources.stream().map( PlatformRole::new ).collect( Collectors.toSet() );
	}

	public Set<PlatformRole> get( Agent agent ) {
		return this.get( agent.getPlatformRoles() );
	}

	public Set<Platform.Role> getRepresentations( Set<PlatformRole> platformRoleResources ) {
		Set<IRI> platformRolesIRIs = IRIUtil.getIRIs( platformRoleResources );
		return RDFNodeUtil.findByIRIs( platformRolesIRIs, Platform.Role.class );
	}
}
