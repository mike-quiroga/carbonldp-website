package com.carbonldp.authorization;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.agents.Agent;
import com.carbonldp.commons.descriptions.ContainerDescription.Type;
import com.carbonldp.commons.models.RDFSource;
import com.carbonldp.ldp.services.ContainerService;
import com.carbonldp.ldp.services.RDFSourceService;
import com.carbonldp.repository.AbstractSesameService;

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

	public Set<PlatformRole> getPlatformRolesOfAgent(Agent agent) {
		Set<URI> platformRolesURIs = containerService.filterMembers(platformRolesContainerURI, agent.getPlatformRoles(), platformRolesContainerType);
		Set<RDFSource> sources = sourceService.get(platformRolesURIs);
		Set<PlatformRole> platformRoles = new HashSet<PlatformRole>();
		for (RDFSource source : sources) {
			platformRoles.add(new PlatformRole(source.getBaseModel(), source.getURI()));
		}
		return platformRoles;
	}
}
