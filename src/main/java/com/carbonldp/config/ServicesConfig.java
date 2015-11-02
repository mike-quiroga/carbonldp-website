package com.carbonldp.config;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.agents.app.AppAgentRepository;
import com.carbonldp.agents.app.AppAgentService;
import com.carbonldp.agents.app.SesameAppAgentService;
import com.carbonldp.agents.platform.PlatformAgentService;
import com.carbonldp.agents.platform.SesamePlatformAgentService;
import com.carbonldp.agents.validators.AgentValidatorRepository;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.AppService;
import com.carbonldp.apps.SesameAppService;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.containers.SesameContainerService;
import com.carbonldp.ldp.nonrdf.NonRDFSourceService;
import com.carbonldp.ldp.nonrdf.SesameNonRDFSourceService;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.ldp.sources.SesameRDFSourceService;
import com.carbonldp.platform.api.PlatformAPIRepository;
import com.carbonldp.platform.api.PlatformAPIService;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.spring.TransactionWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfig {

	@Autowired
	private PlatformAPIRepository platformAPIRepository;

	@Autowired
	private RDFSourceRepository sourceRepository;
	@Autowired
	private ContainerRepository containerRepository;
	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private ACLRepository aclRepository;

	@Autowired
	private RDFResourceRepository resourceRepository;

	@Bean
	public PlatformAPIService platformAPIService() {
		return new PlatformAPIService( platformAPIRepository );
	}

	@Bean
	protected TransactionWrapper transactionWrapper() {
		return new TransactionWrapper();
	}

	@Bean
	public RDFSourceService sourceService() {
		return new SesameRDFSourceService( transactionWrapper(), sourceRepository, containerRepository, aclRepository );
	}

	@Bean
	public ContainerService containerService() {
		return new SesameContainerService( transactionWrapper(), sourceRepository, containerRepository, aclRepository );
	}

	@Bean
	public AppService appService( AppRepository appRepository, AppRoleRepository appRoleRepository, AppAgentRepository appAgentsRepository ) {
		return new SesameAppService( transactionWrapper(), sourceRepository, containerRepository, aclRepository, appRepository, appRoleRepository, appAgentsRepository );
	}

	@Bean
	public PlatformAgentService platformAgentService( AgentRepository platformAgentRepository, AgentValidatorRepository agentValidatorRepository ) {
		return new SesamePlatformAgentService( transactionWrapper(), sourceRepository, containerRepository, aclRepository, platformAgentRepository, agentValidatorRepository );
	}

	@Bean
	public AppAgentService appAgentService( AgentRepository appAgentRepository, AgentValidatorRepository agentValidatorRepository ) {
		return new SesameAppAgentService( transactionWrapper(), sourceRepository, containerRepository, aclRepository, appAgentRepository, agentValidatorRepository );
	}

	//TODO add LocalFileRepository into properties
	@Bean
	public NonRDFSourceService nonRDFResourceService() {
		return new SesameNonRDFSourceService( transactionWrapper(), sourceRepository, containerRepository, aclRepository, fileRepository, resourceRepository );
	}
}
