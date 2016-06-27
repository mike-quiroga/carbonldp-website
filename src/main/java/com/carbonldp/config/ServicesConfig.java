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
import com.carbonldp.authentication.ticket.JWTicketAuthenticationService;
import com.carbonldp.authentication.ticket.TicketService;
import com.carbonldp.authentication.token.JWTokenAuthenticationService;
import com.carbonldp.authentication.token.TokenService;
import com.carbonldp.authorization.acl.ACLPermissionEvaluator;
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
import com.carbonldp.sparql.SPARQLService;
import com.carbonldp.sparql.SesameSPARQLService;
import com.carbonldp.spring.ServicesInvoker;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesConfig {

	@Autowired
	private ACLPermissionEvaluator permissionEvaluator;
	@Autowired
	private SesameConnectionFactory connectionFactory;

	@Bean
	public TokenService tokenService() {
		return new JWTokenAuthenticationService();
	}

	@Bean
	public TicketService ticketService() {
		return new JWTicketAuthenticationService();
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

	@Bean
	public SPARQLService sparqlService() {
		return new SesameSPARQLService( connectionFactory );
	}
}
