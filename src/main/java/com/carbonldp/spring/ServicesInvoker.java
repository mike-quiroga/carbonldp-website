package com.carbonldp.spring;

import com.carbonldp.agents.AgentService;
import com.carbonldp.apps.AppService;
import com.carbonldp.apps.roles.AppRoleService;
import com.carbonldp.authentication.token.TokenService;
import com.carbonldp.authorization.acl.ACLService;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.nonrdf.NonRDFSourceService;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.platform.api.PlatformAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author JorgeEspinosa
 * @since 0.27.0
 */
@Transactional
public class ServicesInvoker {
	private NonRDFSourceService nonRDFResourceService;
	private AgentService appAgentService;
	private AgentService platformAgentService;
	private AppService appService;
	private AppRoleService appRoleService;
	private ContainerService containerService;
	private RDFSourceService sourceService;
	private ACLService aclService;
	private PlatformAPIService platformAPIService;
	private TokenService tokenService;

	public void proxy( Consumer<ServicesInvoker> consumer ) {
		consumer.accept( this );
	}

	public <E> E proxy( Function<ServicesInvoker, E> function ) {
		return function.apply( this );
	}

	public NonRDFSourceService getNonRDFResourceService() {
		return nonRDFResourceService;
	}

	public AgentService getAppAgentService() {
		return appAgentService;
	}

	public AgentService getPlatformAgentService() {
		return platformAgentService;
	}

	public AppService getAppService() {
		return appService;
	}

	public AppRoleService getAppRoleService() {
		return appRoleService;
	}

	public ContainerService getContainerService() {
		return containerService;
	}

	public RDFSourceService getSourceService() {
		return sourceService;
	}

	public ACLService getAclService() {
		return aclService;
	}

	public PlatformAPIService getPlatformAPIService() {
		return platformAPIService;
	}

	public TokenService getTokenService() {
		return tokenService;
	}

	@Autowired
	public void setNonRDFResourceService( NonRDFSourceService nonRDFResourceService ) {this.nonRDFResourceService = nonRDFResourceService; }

	@Autowired
	public void setAppAgentService( AgentService appAgentService ) {this.appAgentService = appAgentService; }

	@Autowired
	public void setPlatformAgentService( AgentService platformAgentService ) { this.platformAgentService = platformAgentService; }

	@Autowired
	public void setAppService( AppService appService ) { this.appService = appService; }

	@Autowired
	public void setAppRoleService( AppRoleService appRoleService ) { this.appRoleService = appRoleService; }

	@Autowired
	public void setContainerService( ContainerService containerService ) { this.containerService = containerService; }

	@Autowired
	public void setSourceService( RDFSourceService sourceService ) { this.sourceService = sourceService; }

	@Autowired
	public void setAclService( ACLService aclService ) { this.aclService = aclService; }

	@Autowired
	public void setPlatformAPIService( PlatformAPIService platformAPIService ) { this.platformAPIService = platformAPIService; }

	@Autowired
	public void setTokenService( TokenService tokenService ) { this.tokenService = tokenService; }
}
