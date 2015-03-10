package com.carbonldp.config;

import com.carbonldp.Vars;
import com.carbonldp.agents.AgentService;
import com.carbonldp.agents.SesameAgentService;
import com.carbonldp.apps.AppService;
import com.carbonldp.apps.SesameAppService;
import com.carbonldp.authorization.PlatformPrivilegeService;
import com.carbonldp.authorization.PlatformRoleService;
import com.carbonldp.authorization.SesamePlatformPrivilegeService;
import com.carbonldp.authorization.SesamePlatformRoleService;
import com.carbonldp.ldp.services.*;
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
	public RDFSourceService sourceService() {
		return new SesameRDFSourceService( connectionFactory, resourceRepository, documentRepository );
	}

	@Bean
	public ContainerService containerService() {
		List<TypedContainerService> typedServices = new ArrayList<TypedContainerService>();
		typedServices.add( new SesameBasicContainerService( connectionFactory, resourceRepository, documentRepository ) );
		typedServices.add( new SesameDirectContainerService( connectionFactory, resourceRepository, documentRepository ) );
		typedServices.add( new SesameIndirectContainerService( connectionFactory, resourceRepository, documentRepository ) );

		return new SesameContainerService( connectionFactory, resourceRepository, documentRepository, typedServices );
	}

	@Bean
	public AgentService agentService() {
		URI agentsContainerURI = new URIImpl( Vars.getAgentsContainerURL() );
		return new SesameAgentService( connectionFactory, sourceService(), containerService(), agentsContainerURI );
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
	public AppService appService() {
		URI appsContainerURI = new URIImpl( Vars.getAppsContainerURL() );
		SesameAppService service = new SesameAppService( connectionFactory, documentRepository, sourceService(), containerService(), appRepositoryService );
		service.setAppsContainerURI( appsContainerURI );
		service.setAppsEntryPoint( Vars.getAppsEntryPointURL() );
		return service;
	}
}
