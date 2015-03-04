package com.carbonldp.repository;

import java.util.ArrayList;
import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.carbonldp.ConfigurationRepository;
import com.carbonldp.agents.AgentService;
import com.carbonldp.agents.SesameAgentService;
import com.carbonldp.apps.AppService;
import com.carbonldp.apps.SesameAppService;
import com.carbonldp.authorization.PlatformPrivilegeService;
import com.carbonldp.authorization.PlatformRoleService;
import com.carbonldp.authorization.SesamePlatformPrivilegeService;
import com.carbonldp.authorization.SesamePlatformRoleService;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.authorization.acl.SesameACLRepository;
import com.carbonldp.ldp.services.ContainerService;
import com.carbonldp.ldp.services.RDFSourceService;
import com.carbonldp.ldp.services.SesameBasicContainerService;
import com.carbonldp.ldp.services.SesameContainerService;
import com.carbonldp.ldp.services.SesameDirectContainerService;
import com.carbonldp.ldp.services.SesameIndirectContainerService;
import com.carbonldp.ldp.services.SesameRDFSourceService;
import com.carbonldp.ldp.services.TypedContainerService;
import com.carbonldp.repository.txn.TxnConfig;

@Configuration
//@formatter:off
@Import(
	value = {
		TxnConfig.class
	}
)
//@formatter:on
public class RepositoryConfig {
	@Autowired
	private ConfigurationRepository configurationRepository;
	@Autowired
	private SesameConnectionFactory connectionFactory;
	@Autowired
	private RepositoryService appRepositoryService;

	@Bean
	protected RDFResourceRepository resourceRepository() {
		return new SesameRDFResourceRepository(connectionFactory);
	}

	@Bean
	protected RDFDocumentRepository documentRepository() {
		return new SesameRDFDocumentRepository(connectionFactory);
	}

	@Bean
	public RDFSourceService sourceService() {
		return new SesameRDFSourceService(connectionFactory, resourceRepository(), documentRepository());
	}

	@Bean
	public ContainerService containerService() {
		List<TypedContainerService> typedServices = new ArrayList<TypedContainerService>();
		typedServices.add(new SesameBasicContainerService(connectionFactory, resourceRepository(), documentRepository()));
		typedServices.add(new SesameDirectContainerService(connectionFactory, resourceRepository(), documentRepository()));
		typedServices.add(new SesameIndirectContainerService(connectionFactory, resourceRepository(), documentRepository()));

		return new SesameContainerService(connectionFactory, resourceRepository(), documentRepository(), typedServices);
	}

	@Bean
	public AgentService agentService() {
		URI agentsContainerURI = new URIImpl(configurationRepository.getPlatformAgentsContainerURL());
		return new SesameAgentService(connectionFactory, sourceService(), containerService(), agentsContainerURI);
	}

	@Bean
	public PlatformRoleService platformRoleService() {
		URI platformRolesContainerURI = new URIImpl(configurationRepository.getPlatformRolesContainerURL());
		return new SesamePlatformRoleService(connectionFactory, sourceService(), containerService(), platformRolesContainerURI);
	}

	@Bean
	public PlatformPrivilegeService platformPrivilegeService() {
		URI platformPrivilegesContainerURI = new URIImpl(configurationRepository.getPlatformPrivilegesContainerURL());
		return new SesamePlatformPrivilegeService(connectionFactory, sourceService(), containerService(), platformPrivilegesContainerURI);
	}

	@Bean
	public AppService appService() {
		URI appsContainerURI = new URIImpl(configurationRepository.getPlatformAppsContainerURL());
		SesameAppService service = new SesameAppService(connectionFactory, documentRepository(), sourceService(), containerService(),
				appRepositoryService);
		service.setAppsContainerURI(appsContainerURI);
		service.setAppsEntryPoint(configurationRepository.getAppsEntryPointURL());
		return service;
	}

	@Bean
	public ACLRepository aclRepository() {
		return new SesameACLRepository(connectionFactory, resourceRepository(), documentRepository());
	}
}
