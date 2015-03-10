package com.carbonldp.config;

import com.carbonldp.Vars;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.agents.SesameAgentRepository;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.SesameAppRepository;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.authorization.SesamePlatformPrivilegeRepository;
import com.carbonldp.authorization.SesamePlatformRoleRepository;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.authorization.acl.SesameACLRepository;
import com.carbonldp.ldp.containers.*;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.ldp.sources.SesameRDFSourceRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.rdf.SesameRDFDocumentRepository;
import com.carbonldp.rdf.SesameRDFResourceRepository;
import com.carbonldp.repository.RepositoryService;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RepositoriesConfig {
	@Autowired
	private SesameConnectionFactory connectionFactory;

	@Bean
	public RDFResourceRepository resourceRepository() {
		Assert.notNull( connectionFactory );
		return new SesameRDFResourceRepository( connectionFactory );
	}

	@Bean
	public RDFDocumentRepository documentRepository() {
		Assert.notNull( connectionFactory );
		return new SesameRDFDocumentRepository( connectionFactory );
	}

	@Bean
	public RDFSourceRepository sourceRepository() {
		Assert.notNull( connectionFactory );
		return new SesameRDFSourceRepository( connectionFactory, resourceRepository(), documentRepository() );
	}

	@Bean
	public ContainerRepository containerRepository() {
		Assert.notNull( connectionFactory );
		List<TypedContainerRepository> typedServices = new ArrayList<TypedContainerRepository>();
		typedServices.add( new SesameBasicContainerRepository( connectionFactory, resourceRepository(), documentRepository() ) );
		typedServices.add( new SesameDirectContainerRepository( connectionFactory, resourceRepository(), documentRepository() ) );
		typedServices.add( new SesameIndirectContainerRepository( connectionFactory, resourceRepository(), documentRepository() ) );

		return new SesameContainerRepository( connectionFactory, resourceRepository(), documentRepository(), typedServices );
	}

	@Bean
	public AgentRepository agentRepository() {
		Assert.notNull( connectionFactory );
		URI agentsContainerURI = new URIImpl( Vars.getAgentsContainerURL() );
		return new SesameAgentRepository( connectionFactory, sourceRepository(), containerRepository(), agentsContainerURI );
	}

	@Bean
	public PlatformRoleRepository platformRoleRepository() {
		Assert.notNull( connectionFactory );
		URI platformRolesContainerURI = new URIImpl( Vars.getRolesContainerURL() );
		return new SesamePlatformRoleRepository( connectionFactory, sourceRepository(), containerRepository(), platformRolesContainerURI );
	}

	@Bean
	public PlatformPrivilegeRepository platformPrivilegeRepository() {
		Assert.notNull( connectionFactory );
		URI platformPrivilegesContainerURI = new URIImpl( Vars.getPrivilegesContainerURL() );
		return new SesamePlatformPrivilegeRepository( connectionFactory, sourceRepository(), containerRepository(), platformPrivilegesContainerURI );
	}

	@Bean
	public AppRepository appRepository( RepositoryService appRepositoryService ) {
		Assert.notNull( connectionFactory );
		Assert.notNull( appRepositoryService );
		URI appsContainerURI = new URIImpl( Vars.getAppsContainerURL() );
		SesameAppRepository service = new SesameAppRepository( connectionFactory, documentRepository(), sourceRepository(), containerRepository(), appRepositoryService );
		service.setAppsContainerURI( appsContainerURI );
		service.setAppsEntryPoint( Vars.getAppsEntryPointURL() );
		return service;
	}

	@Bean
	public ACLRepository aclRepository() {
		return new SesameACLRepository( connectionFactory, resourceRepository(), documentRepository() );
	}

}
