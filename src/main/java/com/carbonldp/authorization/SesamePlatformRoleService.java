package com.carbonldp.authorization;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.agents.Agent;
import com.carbonldp.descriptions.ContainerDescription.Type;
import com.carbonldp.ldp.services.ContainerService;
import com.carbonldp.ldp.services.RDFSourceService;
import com.carbonldp.models.RDFSource;
import com.carbonldp.repository.AbstractSesameService;
import com.carbonldp.utils.RDFNodeUtil;
import com.carbonldp.utils.URIUtil;

public class SesamePlatformRoleService extends AbstractSesameService implements PlatformRoleService {
	private final RDFSourceService sourceService;
	private final ContainerService containerService;
	private final URI platformRolesContainerURI;

	private final Type platformRolesContainerType = Type.BASIC;

	public SesamePlatformRoleService(SesameConnectionFactory connectionFactory, RDFSourceService sourceService, ContainerService containerService,
			URI platformRolesContainerURI) {
		super(connectionFactory);
		this.sourceService = sourceService;
		this.containerService = containerService;
		this.platformRolesContainerURI = platformRolesContainerURI;
	}

	public Set<PlatformRole> get(Agent agent) {
		Set<PlatformRole> platformRoles = new HashSet<PlatformRole>();
		Set<URI> platformRolesURIs = containerService.filterMembers(platformRolesContainerURI, agent.getPlatformRoles(), platformRolesContainerType);
		Set<RDFSource> sources = sourceService.get(platformRolesURIs);
		for (RDFSource source : sources) {
			platformRoles.add(new PlatformRole(source));
		}
		return platformRoles;
	}

	public Set<Platform.Role> getRepresentations(Set<PlatformRole> platformRoleResources) {
		Set<URI> platformRolesURIs = URIUtil.getURIs(platformRoleResources);
		return RDFNodeUtil.findByURIs(platformRolesURIs, Platform.Role.class);
	}
}
