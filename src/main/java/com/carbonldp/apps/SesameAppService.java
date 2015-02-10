package com.carbonldp.apps;

import org.openrdf.model.URI;
import org.openrdf.spring.SesameConnectionFactory;

import com.carbonldp.ldp.services.ContainerService;
import com.carbonldp.ldp.services.RDFSourceService;
import com.carbonldp.repository.AbstractSesameService;

public final class SesameAppService extends AbstractSesameService implements AppService {
	private final RDFSourceService sourceService;
	private final ContainerService containerService;

	public SesameAppService(SesameConnectionFactory connectionFactory, RDFSourceService sourceService, ContainerService containerService) {
		super(connectionFactory);
		this.sourceService = sourceService;
		this.containerService = containerService;
	}

	public Application get(URI appURI) {
		// TODO: Implement
		return null;
	}
}
