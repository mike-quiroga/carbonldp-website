package com.carbonldp.config;

import com.carbonldp.agents.AgentService;
import com.carbonldp.agents.app.SesameAppAgentService;
import com.carbonldp.agents.platform.SesamePlatformAgentService;
import com.carbonldp.apps.AppService;
import com.carbonldp.apps.SesameAppService;
import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.apps.roles.SesameAppRoleService;
import com.carbonldp.authentication.ldapServer.app.LDAPServerService;
import com.carbonldp.authentication.ldapServer.app.SesameLDAPServerService;
import com.carbonldp.authentication.token.JWTAuthenticationService;
import com.carbonldp.authentication.token.TokenService;
import com.carbonldp.authorization.acl.ACLService;
import com.carbonldp.authorization.acl.SesameACLService;
import com.carbonldp.jobs.ExecutionService;
import com.carbonldp.jobs.JobService;
import com.carbonldp.jobs.SesameExecutionService;
import com.carbonldp.jobs.SesameJobService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.containers.SesameContainerService;
import com.carbonldp.ldp.nonrdf.NonRDFSourceService;
import com.carbonldp.ldp.nonrdf.SesameNonRDFSourceService;
import com.carbonldp.ldp.nonrdf.backup.BackupService;
import com.carbonldp.ldp.nonrdf.backup.SesameBackupService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.ldp.sources.SesameRDFSourceService;
import com.carbonldp.platform.api.PlatformAPIService;
import com.carbonldp.spring.ServicesInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;

@Configuration
public class ServicesConfig {

	@Autowired
	private PermissionEvaluator permissionEvaluator;

	@Bean
	public TokenService tokenService() {
		return new JWTAuthenticationService();
	}

	@Bean
	public PlatformAPIService platformAPIService() {
		return new PlatformAPIService();
	}

	@Bean
	protected ServicesInvoker servicesInvoker() {
		return new ServicesInvoker();
	}

	@Bean
	public ACLService aclService() {
		return new SesameACLService( permissionEvaluator );
	}

	@Bean
	public RDFSourceService sourceService() {
		return new SesameRDFSourceService();
	}

	@Bean
	public ContainerService containerService() {
		return new SesameContainerService();
	}

	@Bean
	public AppRoleService appRoleService() {
		return new SesameAppRoleService();
	}

	@Bean
	public AppService appService() {
		return new SesameAppService();
	}

	@Bean
	public AgentService platformAgentService() {
		return new SesamePlatformAgentService();
	}

	@Bean
	public AgentService appAgentService() {
		return new SesameAppAgentService();
	}

	@Bean
	public LDAPServerService ldapServerService() {
		return new SesameLDAPServerService();
	}

	@Bean
	public NonRDFSourceService nonRDFResourceService() {
		return new SesameNonRDFSourceService();
	}

	@Bean
	public JobService jobService() {
		return new SesameJobService();
	}

	@Bean
	public BackupService backupService() {
		return new SesameBackupService();
	}

	@Bean
	public ExecutionService executionService() {
		return new SesameExecutionService();
	}
}
