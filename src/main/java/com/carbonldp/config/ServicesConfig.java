package com.carbonldp.config;

import com.carbonldp.agents.AgentRepository;
import com.carbonldp.agents.AgentService;
import com.carbonldp.agents.app.AppAgentRepository;
import com.carbonldp.agents.app.SesameAppAgentService;
import com.carbonldp.agents.platform.SesamePlatformAgentService;
import com.carbonldp.agents.validators.AgentValidatorRepository;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.AppRole;
import com.carbonldp.apps.AppService;
import com.carbonldp.apps.SesameAppService;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.apps.roles.SesameAppRoleService;
import com.carbonldp.authentication.token.TokenService;
import com.carbonldp.authentication.token.JWTAuthenticationService;
import com.carbonldp.authentication.token.app.AppTokenRepository;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.authorization.acl.ACLService;
import com.carbonldp.authorization.acl.SesameACLService;
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
import org.springframework.security.access.PermissionEvaluator;

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
	private AppTokenRepository appTokenRepository;
	@Autowired
	private RDFResourceRepository resourceRepository;
	@Autowired
	private AppRoleRepository appRoleRepository;

	@Autowired
	private PermissionEvaluator permissionEvaluator;

	@Autowired
	private AppRoleRepository appRoleRepository;

	@Bean
	public TokenService tokenService() {
		return new JWTAuthenticationService();
	}

	@Bean
	public PlatformAPIService platformAPIService() {
		return new PlatformAPIService( platformAPIRepository );
	}

	@Bean
	protected TransactionWrapper transactionWrapper() {
		return new TransactionWrapper();
	}

	@Bean
	public ACLService aclService() {
		return new SesameACLService( transactionWrapper(), sourceRepository, containerRepository, aclRepository, permissionEvaluator, appRoleRepository );
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
		return new SesameAppService( transactionWrapper(), sourceRepository, containerRepository, aclRepository, appRepository, appRoleRepository, appAgentsRepository, appTokenRepository );
	}

	@Bean
	public AgentService platformAgentService( AgentRepository platformAgentRepository, AgentValidatorRepository agentValidatorRepository ) {
		return new SesamePlatformAgentService( transactionWrapper(), sourceRepository, containerRepository, aclRepository, platformAgentRepository, agentValidatorRepository );
	}

	@Bean
	public AgentService appAgentService( AgentRepository appAgentRepository, AgentValidatorRepository agentValidatorRepository ) {
		return new SesameAppAgentService( transactionWrapper(), sourceRepository, containerRepository, aclRepository, appAgentRepository, agentValidatorRepository );
	}

	@Bean
	public AppRoleService appRoleService() {
		return new SesameAppRoleService( transactionWrapper(), sourceRepository, containerRepository, aclRepository, appRoleRepository );
	}

	//TODO add LocalFileRepository into properties
	@Bean
	public NonRDFSourceService nonRDFResourceService() {
		return new SesameNonRDFSourceService( transactionWrapper(), sourceRepository, containerRepository, aclRepository, fileRepository, resourceRepository );
	}
}
