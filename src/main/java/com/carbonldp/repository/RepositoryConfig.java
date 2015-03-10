package com.carbonldp.repository;

import com.carbonldp.authorization.acl.ACLRepository;
import com.carbonldp.authorization.acl.SesameACLRepository;
import com.carbonldp.config.ConfigurationRepository;
import com.carbonldp.config.ServiceConfig;
import org.openrdf.spring.SesameConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(
	value = {
		ServiceConfig.class
	}
)
public class RepositoryConfig {
	@Autowired
	private ConfigurationRepository configurationRepository;
	@Autowired
	private SesameConnectionFactory connectionFactory;

	@Bean
	protected RDFResourceRepository resourceRepository() {
		return new SesameRDFResourceRepository( connectionFactory );
	}

	@Bean
	protected RDFDocumentRepository documentRepository() {
		return new SesameRDFDocumentRepository( connectionFactory );
	}

	@Bean
	public ACLRepository aclRepository() {
		return new SesameACLRepository( connectionFactory, resourceRepository(), documentRepository() );
	}
}
