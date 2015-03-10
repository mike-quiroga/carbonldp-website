package com.carbonldp.config;

import com.carbonldp.Vars;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.agents.SesameAgentRepository;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.SesameAppRepository;
import com.carbonldp.authorization.PlatformPrivilegeService;
import com.carbonldp.authorization.PlatformRoleService;
import com.carbonldp.authorization.SesamePlatformPrivilegeService;
import com.carbonldp.authorization.SesamePlatformRoleService;
import com.carbonldp.ldp.containers.*;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.ldp.sources.SesameRDFSourceRepository;
import com.carbonldp.repository.RDFDocumentRepository;
import com.carbonldp.repository.RDFResourceRepository;
import com.carbonldp.repository.RepositoryService;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ServiceConfig {
	@Autowired
	private SesameConnectionFactory connectionFactory;
	@Autowired
	private RDFResourceRepository resourceRepository;
	@Autowired
	private RDFDocumentRepository documentRepository;
	@Autowired
	private RepositoryService appRepositoryService;

	@Bean
	public RDFSourceRepository sourceService() {
		return new SesameRDFSourceRepository( connectionFactory, resourceRepository, documentRepository );
	}

	@Bean
	public ContainerRepository containerService() {
		List<TypedContainerRepository> typedServices = new ArrayList<TypedContainerRepository>();
		typedServices.add( new SesameBasicContainerRepository( connectionFactory, resourceRepository, documentRepository ) );
		typedServices.add( new SesameDirectContainerRepository( connectionFactory, resourceRepository, documentRepository ) );
		typedServices.add( new SesameIndirectContainerRepository( connectionFactory, resourceRepository, documentRepository ) );

		return new SesameContainerRepository( connectionFactory, resourceRepository, documentRepository, typedServices );
	}

	@Bean
	public AgentRepository agentService() {
		URI agentsContainerURI = new URIImpl( Vars.getAgentsContainerURL() );
		return new SesameAgentRepository( connectionFactory, sourceService(), containerService(), agentsContainerURI );
	}

	@Bean
	public PlatformRoleService platformRoleService() {
		URI platformRolesContainerURI = new URIImpl( Vars.getRolesContainerURL() );
		return new SesamePlatformRoleService( connectionFactory, sourceService(), containerService(), platformRolesContainerURI );
	}

	@Bean
	public PlatformPrivilegeService platformPrivilegeService() {
		URI platformPrivilegesContainerURI = new URIImpl( Vars.getPrivilegesContainerURL() );
		return new SesamePlatformPrivilegeService( connectionFactory, sourceService(), containerService(), platformPrivilegesContainerURI );
	}

	@Bean
	public AppRepository appService() {
		URI appsContainerURI = new URIImpl( Vars.getAppsContainerURL() );
		SesameAppRepository service = new SesameAppRepository( connectionFactory, documentRepository, sourceService(), containerService(), appRepositoryService );
		service.setAppsContainerURI( appsContainerURI );
		service.setAppsEntryPoint( Vars.getAppsEntryPointURL() );
		return service;
	}
}
