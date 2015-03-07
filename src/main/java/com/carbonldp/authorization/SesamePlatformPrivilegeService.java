package com.carbonldp.authorization;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.transaction.annotation.Transactional;

import com.carbonldp.descriptions.ContainerDescription.Type;
import com.carbonldp.ldp.services.ContainerService;
import com.carbonldp.ldp.services.RDFSourceService;
import com.carbonldp.models.RDFSource;
import com.carbonldp.repository.AbstractSesameService;

@Transactional
public class SesamePlatformPrivilegeService extends AbstractSesameService implements PlatformPrivilegeService {
	private final RDFSourceService sourceService;
	private final ContainerService containerService;
	private final URI platformPrivilegesContainerURI;

	private final Type platformPrivilegesContainerType = Type.BASIC;

	public SesamePlatformPrivilegeService(SesameConnectionFactory connectionFactory, RDFSourceService sourceService, ContainerService containerService,
			URI platformPrivilegesContainerURI) {
		super(connectionFactory);
		this.sourceService = sourceService;
		this.containerService = containerService;
		this.platformPrivilegesContainerURI = platformPrivilegesContainerURI;
	}

	public Set<PlatformPrivilege> get(Set<PlatformRole> platformRoles) {
		Set<PlatformPrivilege> privileges = new HashSet<PlatformPrivilege>();
		if ( platformRoles.isEmpty() ) return privileges;

		Set<URI> privilegeURIs = new HashSet<URI>();
		for (PlatformRole role : platformRoles) {
			privilegeURIs.addAll(role.getPrivileges());
		}

		privilegeURIs = containerService.filterMembers(platformPrivilegesContainerURI, privilegeURIs, platformPrivilegesContainerType);
		if ( privilegeURIs.isEmpty() ) return privileges;

		Set<RDFSource> sources = sourceService.get(privilegeURIs);
		for (RDFSource source : sources) {
			privileges.add(new PlatformPrivilege(source.getBaseModel(), source.getURI()));
		}

		return privileges;
	}
}