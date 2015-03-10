package com.carbonldp.config;

import com.carbonldp.apps.AppRepository;
import com.carbonldp.apps.AppService;
import com.carbonldp.apps.SesameAppService;
import com.carbonldp.ldp.containers.ContainerRepository;
import com.carbonldp.ldp.containers.ContainerService;
import com.carbonldp.ldp.containers.SesameContainerService;
import com.carbonldp.ldp.sources.RDFSourceRepository;
import com.carbonldp.ldp.sources.RDFSourceService;
import com.carbonldp.ldp.sources.SesameRDFSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

@Configuration
public class ServicesConfig {

	@Autowired
	private RDFSourceRepository sourceRepository;

	@Autowired
	private ContainerRepository containerRepository;

	@Autowired
	private AppRepository appRepository;

	@Bean
	public RDFSourceService sourceService() {
		Assert.notNull( sourceRepository );
		Assert.notNull( containerRepository );
		return new SesameRDFSourceService( sourceRepository, containerRepository );
	}

	@Bean
	public ContainerService containerService() {
		Assert.notNull( sourceRepository );
		Assert.notNull( containerRepository );
		return new SesameContainerService( sourceRepository, containerRepository );
	}

	@Bean
	public AppService appService() {
		Assert.notNull( sourceRepository );
		Assert.notNull( containerRepository );
		Assert.notNull( appRepository );
		return new SesameAppService( sourceRepository, containerRepository, appRepository );
	}
}
