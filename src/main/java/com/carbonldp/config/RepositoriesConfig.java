package com.carbonldp.config;

import com.carbonldp.Vars;
import com.carbonldp.agents.platform.PlatformAgentRepository;
import com.carbonldp.agents.platform.SesamePlatformAgentRepository;
import com.carbonldp.agents.validators.AgentValidatorRepository;
import com.carbonldp.agents.validators.SesameAgentValidatorRepository;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.SesameAppRepository;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.apps.roles.SesameAppRoleRepository;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.authorization.SesamePlatformPrivilegeRepository;
import com.carbonldp.authorization.SesamePlatformRoleRepository;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.authorization.acl.SesameACLRepository;
import com.carbonldp.ldp.containers.*;
import com.carbonldp.ldp.nonrdf.RDFRepresentationRepository;
import com.carbonldp.ldp.nonrdf.SesameRDFRepresentationRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.ldp.sources.SesameRDFSourceRepository;
import com.carbonldp.platform.api.PlatformAPIRepository;
import com.carbonldp.rdf.RDFDocumentRepository;
import com.carbonldp.rdf.RDFResourceRepository;
import com.carbonldp.rdf.SesameRDFDocumentRepository;
import com.carbonldp.rdf.SesameRDFResourceRepository;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.repository.LocalFileRepository;
import com.carbonldp.repository.RepositoryService;
import com.carbonldp.utils.PropertiesUtil;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
public class RepositoriesConfig {
	@Autowired
	private SesameConnectionFactory connectionFactory;

	@Bean
	public PlatformAPIRepository platformAPIRepository() {
		Resource propertiesFile = new ClassPathResource( "project.properties" );
		PropertiesFactoryBean factory = new PropertiesFactoryBean();
		factory.setLocation( propertiesFile );

		Properties properties;
		try {
			factory.afterPropertiesSet();
			properties = factory.getObject();
		} catch ( IOException e ) {
			throw new BeanInitializationException( "Couldn't load the platform properties file.", e );
		}

		PropertiesUtil.resolveProperties( properties );

		return new PlatformAPIRepository( properties );
	}

	@Bean
	public FileRepository fileRepository() {
		return new LocalFileRepository( connectionFactory );
	}

	@Bean
	public RDFRepresentationRepository rdfRepresentationRepository() {
		return new SesameRDFRepresentationRepository( connectionFactory, resourceRepository(), documentRepository() );
	}

	@Bean
	public RDFResourceRepository resourceRepository() {
		return new SesameRDFResourceRepository( connectionFactory );
	}

	@Bean
	public RDFDocumentRepository documentRepository() {
		return new SesameRDFDocumentRepository( connectionFactory );
	}

	@Bean
	public RDFSourceRepository sourceRepository() {
		return new SesameRDFSourceRepository( connectionFactory, resourceRepository(), documentRepository() );
	}

	@Bean
	public TypedContainerRepository basicContainerRepository() {
		return new SesameBasicContainerRepository( connectionFactory, resourceRepository(), documentRepository() );
	}

	@Bean
	public TypedContainerRepository directContainerRepository() {
		return new SesameDirectContainerRepository( connectionFactory, resourceRepository(), documentRepository() );
	}

	@Bean
	public TypedContainerRepository indirectContainerRepository() {
		return new SesameIndirectContainerRepository( connectionFactory, resourceRepository(), documentRepository() );
	}

	@Bean
	public ContainerRepository containerRepository() {
		List<TypedContainerRepository> typedServices = new ArrayList<>();
		typedServices.add( basicContainerRepository() );
		typedServices.add( directContainerRepository() );
		typedServices.add( indirectContainerRepository() );

		return new SesameContainerRepository( connectionFactory, resourceRepository(), documentRepository(), sourceRepository(), typedServices );
	}

	@Bean
	public PlatformAgentRepository platformAgentRepository() {
		URI agentsContainerURI = new URIImpl( Vars.getAgentsContainerURL() );
		return new SesamePlatformAgentRepository( connectionFactory, sourceRepository(), containerRepository(), agentsContainerURI );
	}

	@Bean
	public AgentValidatorRepository agentValidatorRepository() {
		return new SesameAgentValidatorRepository( connectionFactory, resourceRepository(), documentRepository() );
	}

	@Bean
	public PlatformRoleRepository platformRoleRepository() {
		URI platformRolesContainerURI = new URIImpl( Vars.getRolesContainerURL() );
		return new SesamePlatformRoleRepository( connectionFactory, sourceRepository(), containerRepository(), platformRolesContainerURI );
	}

	@Bean
	public PlatformPrivilegeRepository platformPrivilegeRepository() {
		URI platformPrivilegesContainerURI = new URIImpl( Vars.getPrivilegesContainerURL() );
		return new SesamePlatformPrivilegeRepository( connectionFactory, sourceRepository(), containerRepository(), platformPrivilegesContainerURI );
	}

	@Bean
	public AppRepository appRepository( RepositoryService appRepositoryService ) {
		URI appsContainerURI = new URIImpl( Vars.getAppsContainerURL() );
		SesameAppRepository service = new SesameAppRepository( connectionFactory, documentRepository(), sourceRepository(), containerRepository(), appRepositoryService );
		service.setAppsContainerURI( appsContainerURI );
		service.setAppsEntryPoint( Vars.getAppsEntryPointURL() );
		return service;
	}

	@Bean
	public AppRoleRepository appRoleRepository() {
		SesameAppRoleRepository repository = new SesameAppRoleRepository( connectionFactory, resourceRepository(), documentRepository(), sourceRepository(), containerRepository() );
		repository.setAppRoleContainerSlug( Vars.getAppRolesContainer() );
		repository.setAgentsContainerSlug( Vars.getAppRoleAgentsContainer() );
		return repository;
	}

	@Bean
	public ACLRepository aclRepository() {
		return new SesameACLRepository( connectionFactory, resourceRepository(), documentRepository() );
	}

}
