package com.carbonldp.config;

import com.carbonldp.Vars;
import com.carbonldp.agents.AgentRepository;
import com.carbonldp.agents.app.SesameAppAgentRepository;
import com.carbonldp.agents.platform.PlatformAgentRepository;
import com.carbonldp.agents.platform.SesamePlatformAgentRepository;
import com.carbonldp.agents.validators.AgentValidatorRepository;
import com.carbonldp.agents.validators.SesameAgentValidatorRepository;
import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.SesameAppRepository;
import com.carbonldp.apps.roles.AppRoleRepository;
import com.carbonldp.apps.roles.SesameAppRoleRepository;
import com.carbonldp.authentication.token.app.AppTokenRepository;
import com.carbonldp.authentication.token.app.SesameAppTokenRepository;
import com.carbonldp.authorization.PlatformPrivilegeRepository;
import com.carbonldp.authorization.PlatformRoleRepository;
import com.carbonldp.authorization.SesamePlatformPrivilegeRepository;
import com.carbonldp.authorization.SesamePlatformRoleRepository;
import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.authorization.acl.SesameACLRepository;
import com.carbonldp.jobs.ExecutionRepository;
import com.carbonldp.jobs.SesameExecutionRepository;
import com.carbonldp.ldp.containers.*;
import com.carbonldp.ldp.nonrdf.NonRDFSourceRepository;
import com.carbonldp.ldp.nonrdf.RDFRepresentationRepository;
import com.carbonldp.ldp.nonrdf.SesameNonRDFSourceRepository;
import com.carbonldp.ldp.nonrdf.SesameRDFRepresentationRepository;
import com.carbonldp.ldp.nonrdf.backup.BackupRepository;
import com.carbonldp.ldp.nonrdf.backup.SesameBackupRepository;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.ldp.sources.SesameRDFSourceRepository;
import com.carbonldp.platform.api.PlatformAPIRepository;
import com.carbonldp.rdf.*;
import com.carbonldp.repository.FileRepository;
import com.carbonldp.repository.LocalFileRepository;
import com.carbonldp.repository.RepositoryService;
import com.carbonldp.utils.PropertiesUtil;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.spring.SesameConnectionFactory;
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
		return new LocalFileRepository();
	}

	@Bean
	public RDFRepresentationRepository rdfRepresentationRepository() {
		return new SesameRDFRepresentationRepository( connectionFactory, resourceRepository(), documentRepository(), fileRepository() );
	}

	@Bean
	public RDFResourceRepository resourceRepository() {
		return new SesameRDFResourceRepository( connectionFactory );
	}

	@Bean
	public RDFMapRepository mapRepository() {
		return new SesameRDFMapRepository( connectionFactory, resourceRepository(), documentRepository() );
	}

	@Bean
	public RDFBlankNodeRepository blankNodeRepository() {
		return new SesameRDFBlankNodeRepository( connectionFactory );
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
	public NonRDFSourceRepository nonRdfSourceRepository() {
		return new SesameNonRDFSourceRepository( connectionFactory, resourceRepository(), documentRepository() );
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

		return new SesameContainerRepository( connectionFactory, resourceRepository(), documentRepository(), sourceRepository(), typedServices, rdfRepresentationRepository() );
	}

	@Bean
	public ExecutionRepository executionRepository() {
		return new SesameExecutionRepository( connectionFactory, resourceRepository(), documentRepository() );
	}

	@Bean
	public PlatformAgentRepository platformAgentRepository() {
		IRI agentsContainerIRI = SimpleValueFactory.getInstance().createIRI( Vars.getInstance().getAgentsContainerURL() );
		return new SesamePlatformAgentRepository( connectionFactory, sourceRepository(), containerRepository(), agentsContainerIRI );
	}

	@Bean
	public AgentValidatorRepository agentValidatorRepository() {
		return new SesameAgentValidatorRepository( connectionFactory, resourceRepository(), documentRepository() );
	}

	@Bean
	public PlatformRoleRepository platformRoleRepository() {
		IRI platformRolesContainerIRI = SimpleValueFactory.getInstance().createIRI( Vars.getInstance().getRolesContainerURL() );
		return new SesamePlatformRoleRepository( connectionFactory, sourceRepository(), containerRepository(), platformRolesContainerIRI );
	}

	@Bean
	public PlatformPrivilegeRepository platformPrivilegeRepository() {
		IRI platformPrivilegesContainerIRI = SimpleValueFactory.getInstance().createIRI( Vars.getInstance().getPrivilegesContainerURL() );
		return new SesamePlatformPrivilegeRepository( connectionFactory, sourceRepository(), containerRepository(), platformPrivilegesContainerIRI );
	}

	@Bean
	public AppRepository appRepository( RepositoryService appRepositoryService ) {
		IRI appsContainerIRI = SimpleValueFactory.getInstance().createIRI( Vars.getInstance().getAppsContainerURL() );
		SesameAppRepository service = new SesameAppRepository( connectionFactory, documentRepository(), sourceRepository(), containerRepository(), appRepositoryService );
		service.setAppsContainerIRI( appsContainerIRI );
		service.setAppsEntryPoint( Vars.getInstance().getAppsEntryPointURL() );
		return service;
	}

	@Bean
	public AppRoleRepository appRoleRepository() {
		SesameAppRoleRepository repository = new SesameAppRoleRepository( connectionFactory, resourceRepository(), documentRepository(), sourceRepository(), containerRepository() );
		repository.setAppRoleContainerSlug( Vars.getInstance().getAppRolesContainer() );
		repository.setAgentsContainerSlug( Vars.getInstance().getAppRoleAgentsContainer() );
		return repository;
	}

	@Bean
	public AgentRepository appAgentRepository() {
		SesameAppAgentRepository repository = new SesameAppAgentRepository( connectionFactory, sourceRepository(), containerRepository() );
		repository.setAgentsContainerSlug( Vars.getInstance().getAppAgentsContainer() );
		return repository;
	}

	@Bean
	public AppTokenRepository appTokenRepository() {
		SesameAppTokenRepository repository = new SesameAppTokenRepository( connectionFactory, containerRepository() );
		repository.setTokensContainerSlug( Vars.getInstance().getAppTokensContainer() );
		repository.setTicketsContainerSlug( Vars.getInstance().getAppTicketsContainer() );
		return repository;
	}

	@Bean
	public ACLRepository aclRepository() {
		return new SesameACLRepository( connectionFactory, resourceRepository(), documentRepository(), sourceRepository() );
	}

	@Bean
	public BackupRepository backupRepository() {return new SesameBackupRepository( connectionFactory, resourceRepository(), documentRepository() ); }
}
