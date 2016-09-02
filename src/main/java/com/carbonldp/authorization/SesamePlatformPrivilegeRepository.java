package com.carbonldp.authorization;

import com.carbonldp.ldp.containers.ContainerDescription.Type;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.sources.RDFSource;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.repository.AbstractSesameRepository;
import com.carbonldp.utils.IRIUtil;
import com.carbonldp.utils.RDFNodeUtil;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Transactional
public class SesamePlatformPrivilegeRepository extends AbstractSesameRepository implements PlatformPrivilegeRepository {
	private final RDFSourceRepository sourceService;
	private final ContainerRepository containerRepository;
	private final IRI platformPrivilegesContainerIRI;

	private final Type platformPrivilegesContainerType = Type.BASIC;

	public SesamePlatformPrivilegeRepository( SesameConnectionFactory connectionFactory, RDFSourceRepository sourceService, ContainerRepository containerRepository,
		IRI platformPrivilegesContainerIRI ) {
		super( connectionFactory );
		this.sourceService = sourceService;
		this.containerRepository = containerRepository;
		this.platformPrivilegesContainerIRI = platformPrivilegesContainerIRI;
	}

	public Set<PlatformPrivilege> get( Set<PlatformRole> platformRoles ) {
		Set<PlatformPrivilege> privileges = new HashSet<>();
		if ( platformRoles.isEmpty() ) return privileges;

		Set<IRI> privilegeIRIs = new HashSet<>();
		for ( PlatformRole role : platformRoles ) {
			privilegeIRIs.addAll( role.getPrivileges() );
		}

		privilegeIRIs = containerRepository.filterMembers( platformPrivilegesContainerIRI, privilegeIRIs, platformPrivilegesContainerType );
		if ( privilegeIRIs.isEmpty() ) return privileges;

		Set<RDFSource> sources = sourceService.get( privilegeIRIs );
		for ( RDFSource source : sources ) {
			privileges.add( new PlatformPrivilege( source.getBaseModel(), source.getIRI() ) );
		}

		return privileges;
	}

	public Set<Platform.Privilege> getRepresentations( Set<PlatformPrivilege> platformPrivilegeResources ) {
		Set<IRI> privilegeIRIs = IRIUtil.getIRIs( platformPrivilegeResources );
		return RDFNodeUtil.findByIRIs( privilegeIRIs, Platform.Privilege.class );
	}
}
