package com.carbonldp.repository;

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
import com.carbonldp.ldp.services.RDFSourceService;
import com.carbonldp.ldp.services.SesameRDFSourceService;
import com.carbonldp.repository.sesame.services.SesameRDFDocumentRepository;

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

	@Bean
	protected RDFDocumentRepository rdfDocumentRepository() {
		return new SesameRDFDocumentRepository(connectionFactory);
	}

	@Bean
	public RDFSourceService rdfSourceService() {
		return new SesameRDFSourceService(connectionFactory);
	}

	@Bean
	public AgentService agentService() {
		return new SesameAgentService(connectionFactory, configurationRepository.getPlatformAppsContainerURL());
	}

	@Bean
	public AppService appService() {
		return new SesameAppService(connectionFactory);
	}
}
